package com.eduquest.config;

import com.eduquest.domain.*;
import com.eduquest.domain.Module;
import com.eduquest.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/** Idempotent, classroom-scoped Samacheer curriculum seed data for classes 6-9. */
@Component
public class SamacheerCurriculumSeeder {
    private static final Logger log = LoggerFactory.getLogger(SamacheerCurriculumSeeder.class);
    private static final List<String> GRADES = List.of("6-A", "7-A", "8-A", "9-A");
    private static final Map<String, String> TEACHERS = Map.of("6-A", "teacher_6a", "7-A", "teacher_7a", "8-A", "teacher_8a", "9-A", "teacher_9a");
    private static final Set<String> LEGACY_SEEDED_TITLES = Set.of(
            "Numbers and Fractions", "The Living World", "Vocabulary Building", "Ancient Civilizations",
            "Integers and Operations", "Nutrition in Plants", "Grammar Fundamentals", "Indian Geography",
            "Algebra Basics", "Force and Pressure", "Reading and Vocabulary", "Indian Freedom Movement",
            "Polynomials", "Cell Biology", "Communication Skills", "Indian Constitution");

    private final ClassroomRepository classroomRepository;
    private final TeacherRepository teacherRepository;
    private final ModuleRepository moduleRepository;
    private final ActivityRepository activityRepository;
    private final LessonContentRepository lessonContentRepository;
    private final GameConfigurationRepository gameConfigRepository;

    public SamacheerCurriculumSeeder(ClassroomRepository classroomRepository, TeacherRepository teacherRepository,
            ModuleRepository moduleRepository, ActivityRepository activityRepository,
            LessonContentRepository lessonContentRepository, GameConfigurationRepository gameConfigRepository) {
        this.classroomRepository = classroomRepository;
        this.teacherRepository = teacherRepository;
        this.moduleRepository = moduleRepository;
        this.activityRepository = activityRepository;
        this.lessonContentRepository = lessonContentRepository;
        this.gameConfigRepository = gameConfigRepository;
    }

    /** Used by the opt-in legacy cleanup allowlist so it never removes current seed modules. */
    public static List<String> seededModuleTitles() {
        List<String> titles = new ArrayList<>();
        for (int grade = 6; grade <= 9; grade++) {
            for (Subject subject : curricularSubjects()) {
                for (Topic topic : topics(grade, subject)) titles.add(topic.title());
            }
        }
        return titles;
    }

    @Transactional
    public void seed() {
        archiveReplacedSeedContent();
        int moduleCount = 0;
        for (String classroomName : GRADES) {
            Classroom classroom = classroomRepository.findByName(classroomName).orElse(null);
            if (classroom == null) {
                log.warn("Skipping curriculum for missing classroom {}", classroomName);
                continue;
            }
            Teacher teacher = teacherRepository.findByUserAccountUsername(TEACHERS.get(classroomName)).orElse(null);
            int grade = Integer.parseInt(classroomName.substring(0, 1));
            for (Subject subject : curricularSubjects()) {
                for (Topic topic : topics(grade, subject)) {
                    seedTopic(classroom, teacher, grade, subject, topic);
                    moduleCount++;
                }
            }
        }
        log.info("Curriculum seed complete: {} grade/subject modules, each with one lesson and two games", moduleCount);
    }

    private void archiveReplacedSeedContent() {
        for (Module module : moduleRepository.findAll()) {
            if (!LEGACY_SEEDED_TITLES.contains(module.getTitle())) continue;
            String classroomName = null;
            for (String candidate : GRADES) {
                Classroom candidateClassroom = classroomRepository.findByName(candidate).orElse(null);
                if (candidateClassroom != null && Objects.equals(candidateClassroom.getId(), module.getClassroomId())) {
                    classroomName = candidate;
                    break;
                }
            }
            if (classroomName == null) continue;
            Teacher expectedOwner = teacherRepository.findByUserAccountUsername(TEACHERS.get(classroomName)).orElse(null);
            if (expectedOwner == null || !Objects.equals(expectedOwner.getId(), module.getCreatedByTeacherId())) continue;
            module.setStatus(ActivityStatus.ARCHIVED);
            moduleRepository.save(module);
            for (Activity activity : activityRepository.findByModuleIdOrderByDisplayOrderAsc(module.getId())) {
                activity.setStatus(ActivityStatus.ARCHIVED);
                activity.setVisibleToStudents(false);
                activityRepository.save(activity);
            }
        }
    }

    private void seedTopic(Classroom classroom, Teacher teacher, int grade, Subject subject, Topic topic) {
        Long teacherId = teacher == null ? null : teacher.getId();
        Module module = moduleRepository.findAll().stream()
                .filter(m -> topic.title().equalsIgnoreCase(m.getTitle()) && Objects.equals(classroom.getId(), m.getClassroomId()))
                .findFirst().orElse(null);
        DifficultyLevel level = grade <= 6 ? DifficultyLevel.BEGINNER : grade == 7 ? DifficultyLevel.INTERMEDIATE
                : grade == 8 ? DifficultyLevel.INTERMEDIATE : DifficultyLevel.ADVANCED;
        if (module == null) {
            module = Module.builder().title(topic.title()).description(topic.moduleSummary()).subject(subject)
                    .difficultyLevel(level).estimatedMinutes(35).classroomId(classroom.getId())
                    .status(ActivityStatus.PUBLISHED).createdByTeacherId(teacherId).build();
        } else {
            module.setDescription(topic.moduleSummary());
            module.setSubject(subject);
            module.setDifficultyLevel(level);
            module.setEstimatedMinutes(35);
            module.setStatus(ActivityStatus.PUBLISHED);
        }
        module = moduleRepository.save(module);
        Long moduleId = module.getId();

        Activity lesson = upsertActivity(moduleId, classroom, teacherId, subject, topic.lessonTitle(),
                topic.lessonSummary(), ActivityType.LESSON, 1, 20);
        LessonContent content = lessonContentRepository.findByActivityId(lesson.getId()).orElseGet(() ->
                LessonContent.builder().activityId(lesson.getId()).build());
        content.setContent(buildLessonContent(grade, subject, topic));
        content.setEstimatedMinutes(12 + grade * 2);
        lessonContentRepository.save(content);

        Activity matching = upsertActivity(moduleId, classroom, teacherId, subject,
                topic.title() + " — Concept Match", "Match each question or concept to its correct answer.",
                ActivityType.MATCH_THE_FOLLOWING, 2, 35);
        saveGame(matching, buildMatchConfiguration(topic));

        Activity challenge = upsertActivity(moduleId, classroom, teacherId, subject,
                topic.title() + " — Challenge Round", "Apply the lesson ideas to four curriculum-aligned questions.",
                ActivityType.SHOOT_THE_ANSWER, 3, 45);
        saveGame(challenge, buildChallengeConfiguration(topic));
    }

    private Activity upsertActivity(Long moduleId, Classroom classroom, Long teacherId, Subject subject,
            String title, String description, ActivityType type, int order, int xp) {
        Activity activity = activityRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId).stream()
                .filter(a -> title.equalsIgnoreCase(a.getTitle())).findFirst().orElse(null);
        if (activity == null) {
            activity = Activity.builder().moduleId(moduleId).createdByTeacherId(teacherId).build();
        }
        activity.setTitle(title);
        activity.setDescription(description);
        activity.setSubject(subject);
        activity.setActivityType(type);
        activity.setStatus(ActivityStatus.PUBLISHED);
        activity.setDisplayOrder(order);
        activity.setXpReward(xp);
        activity.setVisibleToStudents(true);
        activity.setAssignedClassroomId(classroom.getId());
        return activityRepository.save(activity);
    }

    private void saveGame(Activity activity, String json) {
        GameConfiguration configuration = gameConfigRepository.findByActivityId(activity.getId())
                .orElseGet(() -> GameConfiguration.builder().activityId(activity.getId()).build());
        configuration.setJsonConfiguration(json);
        gameConfigRepository.save(configuration);
    }

    private String buildMatchConfiguration(Topic topic) {
        StringBuilder json = new StringBuilder("{\"gameType\":\"MATCH_THE_FOLLOWING\",\"instructions\":\"Connect each prompt with the best answer.\",\"questions\":[");
        for (int i = 0; i < topic.questions().size(); i++) {
            Question q = topic.questions().get(i);
            if (i > 0) json.append(',');
            json.append("{\"id\":\"").append(i + 1).append("\",\"prompt\":\"").append(escape(q.prompt()))
                    .append("\",\"correctAnswer\":\"").append(escape(q.answer())).append("\",\"options\":").append(optionsJson(topic, q)).append('}');
        }
        return json.append("]}").toString();
    }

    private String buildChallengeConfiguration(Topic topic) {
        StringBuilder json = new StringBuilder("{\"gameType\":\"SHOOT_THE_ANSWER\",\"instructions\":\"Read each question and choose the answer supported by the lesson.\",\"questions\":[");
        for (int i = 0; i < topic.questions().size(); i++) {
            Question q = topic.questions().get(i);
            if (i > 0) json.append(',');
            json.append("{\"prompt\":\"").append(escape(q.prompt())).append("\",\"correctAnswer\":\"")
                    .append(escape(q.answer())).append("\",\"options\":").append(optionsJson(topic, q)).append('}');
        }
        return json.append("]}").toString();
    }

    private String optionsJson(Topic topic, Question current) {
        LinkedHashSet<String> options = new LinkedHashSet<>();
        options.add(current.answer());
        for (Question q : topic.questions()) if (!q.answer().equals(current.answer())) options.add(q.answer());
        return options.stream().limit(4).map(value -> "\"" + escape(value) + "\"")
                .reduce((a, b) -> a + "," + b).map(s -> "[" + s + "]").orElse("[]");
    }

    private String buildLessonContent(int grade, Subject subject, Topic topic) {
        return "LEARNING GOALS\n" + topic.goals() + "\n\n" +
                "CORE IDEA\n" + topic.explanation() + "\n\n" +
                "WORKED EXAMPLE\n" + topic.example() + "\n\n" +
                "REMEMBER\n" + topic.takeaways() + "\n\n" +
                "CHECK YOUR UNDERSTANDING\n" + topic.questions().stream().map(q -> "• " + q.prompt()).reduce((a, b) -> a + "\n" + b).orElse("") +
                "\n\nClass " + grade + " • " + subjectLabel(subject) + " • Read first, then try the two practice games.";
    }

    private static String escape(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }

    private static String subjectLabel(Subject subject) {
        return switch (subject) {
            case MATHEMATICS -> "Mathematics";
            case SCIENCE -> "Science";
            case ENGLISH -> "English";
            case SOCIAL_SCIENCE -> "Social Science";
            case TAMIL -> "தமிழ் (Tamil)";
            default -> subject.name();
        };
    }

    private static List<Subject> curricularSubjects() {
        return List.of(Subject.MATHEMATICS, Subject.SCIENCE, Subject.ENGLISH, Subject.SOCIAL_SCIENCE, Subject.TAMIL);
    }

    private record Question(String prompt, String answer) {}
    private record Topic(String title, String moduleSummary, String lessonTitle, String lessonSummary,
                         String goals, String explanation, String example, String takeaways, List<Question> questions) {}

    private static Topic topic(String title, String summary, String lessonTitle, String goals, String explanation,
            String example, String takeaways, String... questionAnswerPairs) {
        List<Question> questions = new ArrayList<>();
        for (int i = 0; i + 1 < questionAnswerPairs.length; i += 2) questions.add(new Question(questionAnswerPairs[i], questionAnswerPairs[i + 1]));
        return new Topic(title, summary, lessonTitle, "A focused reading lesson with examples and a short review.", goals, explanation, example, takeaways, questions);
    }

    private static List<Topic> topics(int grade, Subject subject) {
        return switch (grade) {
            case 6 -> class6(subject);
            case 7 -> class7(subject);
            case 8 -> class8Topics(subject);
            default -> class9(subject);
        };
    }

    private static List<Topic> class6(Subject s) {
        return switch (s) {
            case MATHEMATICS -> List.of(
                topic("Fractions in Everyday Life", "Build a first understanding of parts of a whole, equivalent fractions and simple comparisons.", "Parts of a Whole and Equivalent Fractions", "Recognise numerator and denominator; make equivalent fractions; compare familiar fractions.", "A fraction a/b describes a number of equal parts: b tells how many equal parts make the whole, and a tells how many are considered. Equivalent fractions name the same amount because numerator and denominator are multiplied or divided by the same non-zero number. A number line helps compare fractions with the same whole.", "A chocolate bar has 8 equal pieces and 3 are eaten: 3/8 is eaten. Multiplying top and bottom of 1/2 by 4 gives 4/8, so the two fractions describe the same amount.", "The denominator cannot be zero. Compare only fractions that refer to equal-sized wholes. Simplify by dividing both terms by a common factor.", "In 5/8, what does 8 count?", "Equal parts in the whole", "Which fraction equals 1/2?", "3/8", "4/8", "5/8", "6/8", "Which fraction is greater: 1/4 or 3/4?", "3/4", "Simplify 2/6.", "1/3"),
                topic("Perimeter, Area and Measurement", "Measure classroom objects and distinguish boundary length from covered surface.", "Measuring Shapes Around Us", "Choose suitable units; calculate perimeter; find the area of rectangles on a grid.", "Length measures distance and is recorded in units such as centimetres or metres. Perimeter is the total distance around a closed shape. Area measures the surface covered and is counted in square units. For a rectangle, perimeter is 2 × (length + width), while area is length × width.", "A notice board 6 cm long and 4 cm wide on a drawing has perimeter 20 cm and area 24 cm². The different units remind us that one measures a boundary and the other a surface.", "Convert to one unit before adding lengths. Area uses square units. A larger perimeter does not always mean a larger area.", "Find the perimeter of a 6 by 4 rectangle.", "20 cm", "Find its area.", "24 cm²", "What unit suits floor area?", "Square metres", "What does perimeter measure?", "Distance around a shape"));
            case SCIENCE -> List.of(
                topic("Living and Non-living Things", "Observe life processes and classify familiar organisms using evidence.", "How Do We Recognise Living Things?", "Identify common life processes; explain why movement alone is not enough to classify life.", "Living organisms need energy, grow, respire, respond to their surroundings and reproduce. Plants may not walk from place to place, yet they grow, exchange gases and respond to light. A moving machine does not become living: it does not carry out the linked life processes of an organism.", "A seed may appear inactive, but with water, air and suitable warmth it germinates and grows. A toy car moves only when pushed and does not grow or reproduce.", "Use several life processes, not one sign alone. Living things depend on air, water and suitable surroundings.", "Which is a life process?", "Growth", "Does a toy car reproduce?", "No", "What helps a seed germinate?", "Water, air and warmth", "Can a plant respond to light?", "Yes"),
                topic("Materials and Their Properties", "Compare everyday materials and select them for a purpose using observable properties.", "Choosing Materials for a Job", "Describe hardness, flexibility, transparency and water absorption; connect property to use.", "Materials differ in properties. A transparent material lets most light pass; an opaque material blocks it. A flexible material bends without breaking easily, while a hard material resists scratching. These properties help people choose materials safely and reduce waste.", "A window needs transparent glass so light enters and people can see through it. A raincoat uses a flexible, water-resistant material to keep a person dry.", "Test materials fairly and safely. The best material depends on the job; one property does not make a material best for every use.", "Which property lets light pass through?", "Transparency", "Why is a raincoat water-resistant?", "To keep water out", "Is wood usually transparent?", "No", "Name a flexible material.", "Rubber"));
            case ENGLISH -> List.of(
                topic("Reading for Meaning", "Use sequence, context clues and evidence to understand short informational passages.", "Finding Meaning in a Passage", "Identify the main idea; infer a word from nearby clues; support an answer with evidence.", "A paragraph usually develops one main idea using details. Readers can infer an unfamiliar word by checking nearby examples, contrasts or explanations. A good answer points to the passage rather than relying only on a guess.", "In “The path was slippery after rain, so Meena walked cautiously,” the contrast between slippery and walked slowly suggests cautiously means carefully.", "Read the full sentence before using a dictionary. Separate the main idea from supporting details.", "What is the main idea of a paragraph?", "Its central message", "Which clues help infer a word?", "Examples and surrounding details", "Should an answer use passage evidence?", "Yes", "What does sequence show?", "The order of events"),
                topic("Word Families and Sentence Building", "Grow vocabulary through roots and build clear sentences with nouns, verbs and adjectives.", "Words That Work Together", "Recognise common word classes; use a root to connect related words; write a complete sentence.", "A noun names a person, place or thing; a verb expresses an action or state; an adjective describes a noun. Related words often share a root, such as help, helpful and helpless. Word endings change meaning or grammatical role.", "In “The helpful student carried books,” student is a noun, carried is a verb and helpful describes student. The sentence has a subject and a complete action.", "Choose a word form that fits its job. A complete sentence begins with a capital letter and ends with suitable punctuation.", "Which word is the verb in “Birds build nests”?", "build", "What does an adjective describe?", "A noun", "Which word shares the root help?", "helpful", "What completes a sentence?", "A subject and a predicate"));
            case SOCIAL_SCIENCE -> List.of(
                topic("Maps, Directions and Scale", "Read simple maps using direction, symbols and an introductory scale.", "Finding Places on a Map", "Use compass directions; interpret a legend; estimate a real distance from a simple scale.", "A map is a plan view of a place. A legend explains symbols, and a compass rose shows direction. A scale relates a map distance to a real distance, allowing a reader to estimate how far places are apart.", "If 1 cm on a map represents 2 km, a 3 cm route represents 6 km. North is usually shown at the top, but always check the compass rose.", "Symbols save space but need a legend. Scale calculations use the same units before conversion.", "What explains map symbols?", "The legend", "Which direction is usually at the top?", "North", "If 1 cm means 2 km, what does 3 cm mean?", "6 km", "What does a scale compare?", "Map distance and real distance"),
                topic("Local Government and Community", "Understand how local bodies respond to shared needs and how citizens participate.", "How a Community Makes Decisions", "Name local services; distinguish a local body from a state government; describe participation.", "Local self-government helps people address nearby needs such as roads, streetlights, drainage, sanitation and water supply. Elected representatives raise community concerns, while officials help deliver services. Citizens can participate by attending meetings, reporting problems and using public services responsibly.", "A blocked drain is a local issue: residents can report it to the relevant local body, which can inspect and arrange maintenance.", "Public services are shared responsibilities. Participation and clear evidence help communities set priorities.", "Who handles many neighbourhood services?", "A local body", "Name one local public service.", "Sanitation", "How can residents raise an issue?", "Report it to the local body", "Why are meetings useful?", "They let people discuss shared needs"));
            case TAMIL -> List.of(
                topic("சொற்களின் பொருள் மற்றும் இணைச்சொற்கள்", "அன்றாடத் தமிழ்ச் சொற்களின் பொருளையும் சூழலுக்கேற்ற இணைச்சொற்களையும் அறிதல்.", "சொல்லும் பொருளும்", "சொற்களின் பொருளைச் சூழலால் அறிதல்; பொருத்தமான இணைச்சொல்லைப் பயன்படுத்துதல்.", "ஒரு சொல்லின் பொருள் அது வரும் வாக்கியச் சூழலால் தெளிவாகிறது. ஒரே அல்லது நெருக்கமான பொருளைக் குறிக்கும் சொற்கள் இணைச்சொற்கள் எனப்படும். எல்லா இணைச்சொற்களையும் எல்லா இடங்களிலும் மாற்றிப் பயன்படுத்த முடியாது; வாக்கியத்தின் பொருளும் மரியாதைத் தன்மையும் பொருந்த வேண்டும்.", "“மகிழ்ச்சி” என்பதற்கு “உவகை” நெருக்கமான பொருள் தரும். “மாணவர்கள் வெற்றி பெற்று மகிழ்ந்தனர்” என்ற வாக்கியத்தில் சூழல் அந்த உணர்வை விளக்குகிறது.", "புதிய சொல்லை வாக்கியத்தில் வைத்து பொருள் சரிபார்க்கவும். எழுத்துப் பிழையின்றி எழுதவும்.", "“மகிழ்ச்சி” என்பதற்கு இணையான சொல் எது?", "உவகை", "சொல்லின் பொருளை எது தெளிவாக்கும்?", "வாக்கியச் சூழல்", "இணைச்சொற்கள் எத்தகைய பொருளைக் குறிக்கும்?", "ஒரே அல்லது நெருக்கமான பொருள்", "சொல்லை எங்கு வைத்து சரிபார்க்கலாம்?", "வாக்கியத்தில்"),
                topic("பெயர்ச்சொல் மற்றும் வினைச்சொல்", "எளிய வாக்கியங்களில் பெயரையும் செயலையும் கண்டறிந்து சரியான வாக்கியம் அமைத்தல்.", "வாக்கியத்தில் பெயரும் செயலும்", "பெயர்ச்சொல்லை அடையாளம் காணுதல்; வினைச்சொல்லைக் கண்டறிதல்; முழுமையான வாக்கியம் எழுதுதல்.", "ஒரு நபர், இடம், பொருள் அல்லது உயிரினத்தின் பெயரைப் பெயர்ச்சொல் குறிக்கும். செயல் அல்லது நிலையை வினைச்சொல் காட்டும். வாக்கியத்தில் இவை பொருத்தமாக அமைந்தால் கருத்து தெளிவாகிறது.", "“கயல் புத்தகம் படித்தாள்” என்ற வாக்கியத்தில் “கயல்” பெயர்ச்சொல்; “படித்தாள்” வினைச்சொல். செய்பவருக்கும் வினைக்கும் பொருத்தம் இருக்கிறது.", "வாக்கியத்தை வாசித்து யார் அல்லது எது என்று கேளுங்கள்; பின்னர் என்ன செய்கிறார் என்று கண்டறியுங்கள்.", "“மரம் வளர்கிறது” என்பதில் பெயர்ச்சொல் எது?", "மரம்", "“மரம் வளர்கிறது” என்பதில் வினைச்சொல் எது?", "வளர்கிறது", "பெயர்ச்சொல் எதைக் குறிக்கும்?", "நபர், இடம் அல்லது பொருளின் பெயர்", "வினைச்சொல் எதைக் காட்டும்?", "செயல் அல்லது நிலை"));
            default -> throw new IllegalArgumentException("Unsupported subject " + s);
        };
    }

    private static List<Topic> class7(Subject s) {
        return switch (s) {
            case MATHEMATICS -> List.of(
                topic("Integers and Number Line", "Compare positive and negative integers and model addition and subtraction on a number line.", "Operations with Integers", "Order integers; add and subtract using direction and distance; explain the zero pair.", "Integers include positive whole numbers, zero and negative whole numbers. On a number line, values increase to the right. Adding a negative moves left; subtracting a negative is equivalent to adding its positive opposite.", "Starting at −2 and moving 5 places right gives −2 + 5 = 3. The opposite of −4 is 4, so 6 − (−4) = 10.", "A number farther left is smaller. Keep track of direction before calculating with signs.", "Which is greater, −3 or −7?", "−3", "Calculate −2 + 5.", "3", "Calculate 6 − (−4).", "10", "What is the opposite of −8?", "8"),
                topic("Fractions, Decimals and Percentages", "Connect equivalent fractions, decimal notation and simple percentages in practical contexts.", "Three Ways to Describe a Quantity", "Convert familiar fractions to decimals; interpret percent as per hundred; solve simple comparisons.", "Fractions, decimals and percentages can describe the same proportion. A percent is a number of parts per hundred. Divide the numerator by the denominator to write a fraction as a decimal, and multiply a decimal by 100 to express it as a percent.", "3/4 = 0.75 = 75%. A 25% discount on ₹200 is 25/100 × 200 = ₹50, so the sale price is ₹150.", "Use the same whole when comparing proportions. A discount amount is not the same as the final price.", "What is 3/4 as a decimal?", "0.75", "What does percent mean?", "Per hundred", "Find 25% of 200.", "50", "What is 0.4 as a percentage?", "40%"));
            case SCIENCE -> List.of(
                topic("Nutrition in Plants", "Explain how green plants make food and how water and minerals reach leaves.", "Photosynthesis and Plant Nutrition", "Identify inputs and outputs of photosynthesis; explain chlorophyll and stomata roles.", "Green plants make glucose from carbon dioxide and water using light energy captured by chlorophyll. Oxygen is released. Roots absorb water and minerals, while stomata in leaves allow gas exchange. Plants are producers because they make food that supports other organisms.", "A plant kept in darkness for a long time cannot photosynthesise at the usual rate because light energy is unavailable, even if water is present.", "Photosynthesis needs light, chlorophyll, carbon dioxide and water. Do not confuse plant food with minerals absorbed from soil.", "Which pigment absorbs light?", "Chlorophyll", "Which gas enters leaves for photosynthesis?", "Carbon dioxide", "What gas is released?", "Oxygen", "Which plant part absorbs most water?", "Roots"),
                topic("Heat, Temperature and Transfer", "Distinguish temperature from heat transfer and compare conduction, convection and radiation.", "How Heat Moves", "Use a thermometer idea; identify three transfer methods; explain why materials warm differently.", "Temperature indicates how hot or cold an object is; heat is energy transferred because of a temperature difference. Conduction transfers energy through particle interactions, convection moves warmer fluids, and radiation transfers energy without a material medium.", "A metal spoon in hot soup warms mainly by conduction. Warm air rising above a heater is convection. Sunlight reaches Earth by radiation.", "Heat flows from warmer to cooler regions. Insulators slow transfer; they do not create cold.", "How does heat move through a metal spoon?", "Conduction", "Which transfer can cross space?", "Radiation", "Why does warm air rise?", "Convection", "What does an insulator do?", "Slows heat transfer"));
            case ENGLISH -> List.of(
                topic("Parts of Speech in Context", "Identify how nouns, pronouns, verbs, adjectives and adverbs work within real sentences.", "Grammar Through Meaning", "Classify key word roles; improve agreement; revise a sentence for clarity.", "A word's role depends on how it is used. Pronouns replace nouns, adjectives describe nouns, and adverbs can describe verbs. Subject–verb agreement helps a reader follow who acts. Grammar is most useful when it makes meaning precise.", "In “They carefully measured the narrow path,” they is a pronoun, carefully modifies measured, and narrow describes path.", "Classify words in context rather than memorising lists alone. Check that the subject and verb agree.", "Which word modifies “measured” in the example?", "carefully", "What can a pronoun replace?", "A noun", "Which word describes path?", "narrow", "What should agree in a sentence?", "Subject and verb"),
                topic("Paragraphs and Evidence", "Write a focused paragraph with a topic sentence, supporting detail and a concluding idea.", "Building a Clear Paragraph", "Locate a main claim; select relevant evidence; connect ideas with transitions.", "A strong paragraph develops one controlling idea. The topic sentence states it, supporting sentences explain it with examples or evidence, and a concluding sentence reinforces the point. Transitions show relationships such as addition, contrast or cause.", "Claim: “School gardens support learning.” Evidence: pupils observe plant growth directly. Explanation: this connects science vocabulary to visible change.", "Evidence must support the claim. Remove details that distract from the paragraph's central idea.", "What does a topic sentence state?", "The paragraph's main idea", "What should supporting evidence do?", "Support the claim", "Which word can show contrast?", "however", "What should a paragraph focus on?", "One controlling idea"));
            case SOCIAL_SCIENCE -> List.of(
                topic("India's Physical Features and Rivers", "Connect mountains, plains and river basins with settlement and livelihoods.", "Landforms Shape Life", "Identify major physical regions; explain how rivers support people; read a simple map key.", "The Himalayas, northern plains, plateau, desert and coastal regions have different landforms and resources. Rivers carry water and sediment, support farming and settlements, and can also flood. Geography influences livelihoods but does not determine every choice a community makes.", "Alluvial soil deposited by rivers makes parts of the northern plains suitable for farming, while careful water management is still needed.", "A river basin is land drained by a river and its tributaries. Use map direction and legend when comparing regions.", "What is land drained by a river called?", "A river basin", "What do rivers deposit on plains?", "Alluvial sediment", "Name one coastal livelihood.", "Fishing", "Can rivers also create risk?", "Flooding"),
                topic("Medieval South Indian Kingdoms", "Study how Chola administration, irrigation and trade shaped society in South India.", "The Cholas and Their Public Works", "Identify a Chola contribution; explain local administration; connect tanks and canals with agriculture.", "The Cholas built a powerful South Indian kingdom and supported temples, ports and irrigation works. Local assemblies helped manage village affairs, while tanks and canals stored and distributed water. Inscriptions and monuments provide evidence, but historians compare many sources to interpret the past.", "An inscription recording a donation can reveal who supported a public work, while the structure itself shows how it was built.", "Historical claims need evidence. Irrigation linked engineering, agriculture and community organisation.", "What did tanks help manage?", "Water for agriculture", "What can an inscription provide?", "Historical evidence", "Name a Chola public work.", "A tank or canal", "Why compare sources?", "To check and deepen interpretations"));
            case TAMIL -> List.of(
                topic("தமிழ் இலக்கணம்: பெயர்ச்சொல், வினைச்சொல்", "வாக்கியப் பயன்பாட்டில் பெயர்ச்சொல் மற்றும் வினைச்சொல்லை வேறுபடுத்துதல்.", "சொற்களின் இலக்கணப் பணி", "வாக்கியத்தில் பெயர், செயல் ஆகியவற்றைக் கண்டறிதல்; சொல் பொருத்தத்துடன் எழுதுதல்.", "ஒரு பொருள், உயிர், இடம் அல்லது கருத்தின் பெயரைப் பெயர்ச்சொல் குறிக்கிறது. செயல் அல்லது நிலையை வினைச்சொல் காட்டுகிறது. காலம் மற்றும் எழுவாய்க்கேற்ப வினைச்சொல் மாறலாம். வாக்கியத்தைப் பொருளுடன் வாசிப்பது சொற்களின் பணியை அறிய உதவும்.", "“மாணவர்கள் தோட்டத்தில் செடிகளை நட்டனர்.” இதில் “மாணவர்கள்” பெயர்ச்சொல்; “நட்டனர்” கடந்தகால வினைச்சொல்.", "பெயரைக் கேளுங்கள்: யார் அல்லது எது? செயலைக் கேளுங்கள்: என்ன செய்கிறது? காலத்திற்கேற்ப வினைச்சொல் மாறுவதை கவனிக்கவும்.", "“பறவை பறக்கிறது” என்பதில் வினைச்சொல் எது?", "பறக்கிறது", "“மாணவர்கள் படித்தனர்” எந்தக் காலம்?", "இறந்தகாலம்", "பெயர்ச்சொல் எதைக் குறிக்கும்?", "பெயர் அல்லது பொருள்", "வினைச்சொல் எதைக் காட்டும்?", "செயல் அல்லது நிலை"),
                topic("தமிழ் வாசிப்பு: கருத்தும் சான்றும்", "சிறு உரையின் மையக் கருத்தையும் அதை ஆதரிக்கும் தகவல்களையும் கண்டறிதல்.", "உரையைப் புரிந்து வாசித்தல்", "மையக் கருத்து கண்டறிதல்; நிகழ்வுகளின் வரிசை அமைத்தல்; உரையிலிருந்து சான்று கூறுதல்.", "ஒரு உரையின் மையக் கருத்து அது சொல்லும் முக்கியமான செய்தி. துணை விவரங்கள் அந்தக் கருத்தை விளக்குகின்றன. காலச்சொற்கள் மற்றும் இணைப்புச் சொற்கள் நிகழ்வுகளின் வரிசையையும் காரணத்தையும் காட்டுகின்றன.", "ஒரு பத்தி மரம் நடுதலின் பயனைச் சொன்னால், நிழல் மற்றும் தூய காற்று பற்றிய வாக்கியங்கள் அதற்கான துணைச் சான்றுகளாகும்.", "பதில் எழுதுவதற்கு முன் உரையை முழுமையாக வாசிக்கவும். கருத்தை உரையிலுள்ள தகவலால் ஆதரிக்கவும்.", "உரையின் முக்கியச் செய்தி எது?", "மையக் கருத்து", "கருத்தை விளக்கும் தகவல் எது?", "துணை விவரம்", "நிகழ்வுகளின் வரிசையை எது காட்டும்?", "காலச்சொற்கள்", "பதில் எதனால் ஆதரிக்கப்பட வேண்டும்?", "உரைச் சான்று"));
            default -> throw new IllegalArgumentException("Unsupported class 7 subject: " + s);
        };
    }

    private static List<Topic> class8Topics(Subject s) {
        return switch (s) {
            case MATHEMATICS -> List.of(
                topic("Linear Equations in One Variable", "Translate a situation into a one-variable equation and solve while preserving equality.", "From a Situation to an Equation", "Form equations; use inverse operations; verify a solution by substitution.", "An equation states that two expressions are equal. Applying the same operation to both sides preserves equality. In a linear equation, the variable has power one. After isolating the variable, substitute the result into the original statement to check it.", "If 3x + 5 = 20, subtract 5 on both sides to get 3x = 15, then divide by 3: x = 5. Substitution gives 3(5)+5=20.", "Keep both sides balanced. Write the operation at each step and check the final value in the original equation.", "Solve 3x + 5 = 20.", "5", "What should be done to both sides?", "The same operation", "What is the power of x in a linear equation?", "1", "How do you verify a solution?", "Substitute it into the original equation"),
                topic("Exponents, Squares and Cubes", "Use exponent laws for positive integer powers and recognise square and cube numbers.", "Patterns in Powers", "Interpret a power; multiply powers with the same base; distinguish square from cube.", "The expression aⁿ means multiplying a by itself n times. For the same non-zero base, aᵐ × aⁿ = aᵐ⁺ⁿ. A square number is n² and a cube number is n³. These patterns support mental calculation and measurement problems.", "2³ × 2² = 2⁵ = 32. Also, 4³ = 4 × 4 × 4 = 64, which describes a cube with side length 4 units as 64 cubic units.", "Add exponents only when multiplying equal bases. Square units measure area; cubic units measure volume.", "What is 2³?", "8", "Simplify 3² × 3³.", "3⁵", "What is 4³?", "64", "Which power describes a square number?", "n²"));
            case SCIENCE -> List.of(
                topic("Force, Pressure and Friction", "Use force and area to explain pressure and examine friction in everyday design.", "Why Area Changes Pressure", "Calculate pressure as force per area; identify friction effects; suggest ways to alter friction.", "A force is a push or pull that can change motion or shape. Pressure is force divided by contact area, so the same force produces greater pressure over a smaller area. Friction opposes relative motion between surfaces; it can be useful for grip and harmful through wear or heating.", "A 60 N force over 0.03 m² gives 2,000 Pa. A wide school-bag strap spreads force over a larger area and reduces pressure on the shoulder.", "Use P=F/A with SI units. Friction is not always unwanted: walking and braking depend on it.", "Calculate pressure for 60 N over 0.03 m².", "2,000 Pa", "What happens to pressure if area decreases?", "It increases", "Name a useful effect of friction.", "Grip while walking", "What does friction oppose?", "Relative motion"),
                topic("Microorganisms and Health", "Classify useful and harmful microorganisms and connect hygiene with disease prevention.", "The Invisible World Around Us", "Recognise major microorganism groups; explain useful roles; describe ways infections spread.", "Microorganisms include bacteria, fungi, protozoa and some algae; viruses reproduce only inside host cells. Some microbes help make curd, decompose waste or support ecosystems. Others cause disease. Infection can spread through air, water, food, surfaces or vectors, so prevention depends on the route.", "Fermentation by useful bacteria changes milk into curd. Safe water and handwashing reduce the spread of several water- and contact-borne infections.", "Not every microorganism is harmful. Use prescribed medicines responsibly; antibiotics do not treat viral infections.", "Which group includes yeast?", "Fungi", "How does curd form?", "Useful bacteria ferment milk", "Do antibiotics treat viruses?", "No", "Name one prevention measure.", "Handwashing"));
            case ENGLISH -> List.of(
                topic("Active and Passive Voice", "Choose active or passive constructions based on whether the doer or action needs emphasis.", "Changing Voice Without Changing Meaning", "Identify subject and object; transform a simple tense; preserve the original meaning.", "In active voice, the subject performs the action. In passive voice, the receiver becomes the subject and a form of be is combined with the past participle. The doer can be included with by, but may be omitted when unknown or unimportant.", "Active: “The team planted saplings.” Passive: “Saplings were planted by the team.” The tense remains past and the action is unchanged.", "Match the be verb to tense and number. Use passive voice when the receiver or result deserves emphasis.", "Who performs the action in active voice?", "The subject", "What form follows the be verb in passive voice?", "Past participle", "Convert “They built a bridge.”", "A bridge was built by them", "When may the doer be omitted?", "When unknown or unimportant"),
                topic("Inference, Tone and Figurative Language", "Infer a writer's viewpoint using evidence, word choice and common figures of speech.", "Reading Beyond Literal Meaning", "Distinguish fact from inference; identify tone; explain a metaphor using context.", "Readers infer ideas that are suggested rather than directly stated. Tone is the writer's attitude, conveyed through word choice and detail. A metaphor describes one thing as another to highlight a shared quality; it is not meant literally.", "“The classroom was a beehive before the exhibition” suggests lively activity, not literal bees. The surrounding details help justify the inference.", "Quote or identify the clue behind an inference. Explain the effect of figurative language rather than only naming it.", "What is an inference based on?", "Text clues and reasoning", "What does tone express?", "The writer's attitude", "Is a metaphor usually literal?", "No", "What supports a reading answer?", "Evidence from the text"));
            case SOCIAL_SCIENCE -> List.of(
                topic("Resources, Agriculture and Sustainability", "Trace how resources support livelihoods and how conservation balances present and future needs.", "Using Resources Responsibly", "Classify resources; connect farming choices to climate and soil; propose sustainable practices.", "Resources become useful when people have the knowledge and technology to use them. Agriculture depends on soil, water, climate, labour and markets. Sustainable use meets present needs while maintaining ecosystems and options for future generations.", "Drip irrigation delivers water near plant roots and can reduce evaporation compared with flooding a whole field. Its suitability depends on crop, cost and local conditions.", "Conservation needs local evidence. A solution should consider environmental, economic and social effects.", "What makes something a resource?", "Its usefulness to people", "Name a factor affecting crops.", "Soil or climate", "What does drip irrigation target?", "Plant roots", "What does sustainable use consider?", "Present and future needs"),
                topic("The Constitution and Secular Democracy", "Explore constitutional values, equality before law and the role of public institutions.", "Rules That Protect Rights", "Explain why a constitution limits power; connect equality to laws; distinguish institutions' roles.", "A constitution sets basic rules for government and protects rights. Democratic institutions distribute responsibilities and provide ways to question decisions. Secular government treats people of different religions fairly; equality before law means public rules apply without arbitrary discrimination.", "If a public rule affects everyone, it should be applied consistently and can be reviewed through lawful institutions.", "Rights come with duties to respect others. A constitutional claim should be connected to a principle or institution.", "What does a constitution establish?", "Basic rules for government", "What does equality before law require?", "Fair and consistent application", "What does secular government mean?", "Fair treatment across religions", "How can decisions be reviewed?", "Through lawful institutions"));
            case TAMIL -> List.of(
                topic("தமிழ் இலக்கணம்: காலம் மற்றும் வினைமுற்று", "காலத்திற்கேற்ப வினைச்சொல் மாறுபாட்டையும் வாக்கியப் பொருளையும் ஆராய்தல்.", "வினைச்சொல்லின் காலம்", "கடந்த, நிகழ், எதிர்காலத்தை அடையாளம் காணுதல்; எழுவாய்–வினை பொருத்தம் பேணுதல்.", "ஒரு செயல் நடந்த காலத்தை வினைச்சொல் வெளிப்படுத்துகிறது. இறந்தகாலம் முடிந்த செயலை, நிகழ்காலம் நடைபெறும் செயலை, எதிர்காலம் இனி நிகழவிருக்கும் செயலைக் குறிக்கும். எழுவாயின் எண் மற்றும் பொருளுக்கு ஏற்ற வினைமுற்று வாக்கியத்தைத் தெளிவாக்குகிறது.", "“அவன் எழுதினான்” இறந்தகாலம்; “அவன் எழுதுகிறான்” நிகழ்காலம்; “அவன் எழுதுவான்” எதிர்காலம்.", "காலக் குறியீடுகளை கவனிக்கவும். வாக்கியத்தின் எழுவாயுடன் வினைச்சொல் பொருந்துகிறதா எனச் சரிபார்க்கவும்.", "“அவள் பாடினாள்” எந்தக் காலம்?", "இறந்தகாலம்", "“நாம் படிப்போம்” எந்தக் காலம்?", "எதிர்காலம்", "நிகழ்கால வினைச்சொல் எது?", "விளையாடுகிறான்", "வினைச்சொல் எதனுடன் பொருந்த வேண்டும்?", "எழுவாய்"),
                topic("தமிழ் இலக்கியம்: உவமை மற்றும் உருவகம்", "கவிதை மொழியில் உவமை, உருவகம் ஆகியவை தரும் காட்சியையும் உணர்வையும் புரிதல்.", "அணிநயத்தின் பொருள்", "உவமை மற்றும் உருவகத்தை வேறுபடுத்துதல்; ஒப்பீட்டின் நோக்கத்தை விளக்குதல்.", "இரு பொருள்களின் ஒற்றுமையை வெளிப்படையாகச் சொல்லும் அணி உவமை. ஒரு பொருளை மற்றொன்றாகவே கூறி வலிமையான காட்சியை உருவாக்குவது உருவகம். இவை கருத்தைச் சுருக்கமாகவும் நினைவில் நிற்குமாறும் வெளிப்படுத்துகின்றன.", "“அவள் முகம் நிலவைப் போன்றது” என்பது உவமை; “அவள் முகம் நிலவு” என்பது உருவகம். இரண்டும் அழகைச் சுட்டினாலும் சொல்லும் முறை வேறுபடும்.", "உவமையில் போன்ற, போல் போன்ற குறிகள் வரலாம். உருவகத்தில் நேரடி ஒப்பீடு போலக் கூறப்படும்.", "“போல்” என்று ஒப்பிடுவது எது?", "உவமை", "“அவன் சிங்கம்” என்பது எது?", "உருவகம்", "அணிகள் எதை வளப்படுத்தும்?", "கருத்து வெளிப்பாட்டை", "உவமை எதை வெளிப்படுத்தும்?", "இரு பொருள்களின் ஒற்றுமை"));
            default -> throw new IllegalArgumentException("Unsupported class 8 subject: " + s);
        };
    }

    private static List<Topic> class9(Subject s) {
        return switch (s) {
            case MATHEMATICS -> List.of(
                topic("Polynomials and Factorisation", "Classify polynomials, evaluate expressions and factorise common algebraic forms.", "Polynomial Structure and Identities", "Use degree and coefficients; evaluate at a value; apply common factor and identities.", "A polynomial is a finite sum of terms with non-negative integer powers of a variable. Its degree is the greatest exponent with a non-zero coefficient. Factoring rewrites an expression as a product and can reveal roots or simplify calculations. Identities such as a²−b²=(a−b)(a+b) are valid for all values of a and b.", "For x²−9, recognise a difference of squares: x²−3²=(x−3)(x+3). At x=5, the expression equals 16, matching (2)(8).", "Combine like terms only. Check a factorisation by expanding it and comparing with the original polynomial.", "What is the degree of 4x³−2x+1?", "3", "Factor x²−9.", "(x−3)(x+3)", "What is the coefficient of x in 4x³−2x+1?", "−2", "How can you check a factorisation?", "Expand the factors"),
                topic("Coordinate Geometry and Linear Graphs", "Plot ordered pairs and interpret the relationship represented by a straight-line graph.", "Reading Relationships on a Coordinate Plane", "Locate points in four quadrants; make a table; interpret slope as rate of change.", "An ordered pair (x,y) identifies a point using horizontal and vertical coordinates. A linear relationship has a constant rate of change and graphs as a straight line. In y=mx+c, m is the slope and c is the y-intercept. The graph can reveal whether a relationship increases, decreases or stays constant.", "For y=2x+1, x=0 gives y=1 and x=2 gives y=5. The slope 2 means y increases by 2 for each unit increase in x.", "Plot both coordinates in the correct order. A graph shows a model; check whether extrapolating it makes sense in context.", "What does an ordered pair locate?", "A point on the plane", "In y=2x+1, what is the slope?", "2", "What is the y-intercept?", "1", "What shape graphs a linear relation?", "A straight line"));
            case SCIENCE -> List.of(
                topic("Cells, Tissues and Organ Systems", "Explain how specialised cells organise into tissues and systems in plants and animals.", "From Cells to Organisms", "Name major cell structures; trace organisation from cell to organism; compare cell functions.", "The cell is the basic structural and functional unit of living organisms. Cells with related structures perform a shared function in a tissue; tissues form organs, and organs work together in systems. The nucleus contains genetic material, the cell membrane regulates exchange and mitochondria release usable energy through respiration.", "Muscle cells form muscle tissue; tissues make an organ such as the heart; the heart works with blood vessels in the circulatory system.", "Structure supports function. Plant cells have a cellulose cell wall and chloroplasts in photosynthetic tissues; not every plant cell has chloroplasts.", "What is the basic unit of life?", "Cell", "What level comes after tissue?", "Organ", "Which structure controls many cell activities?", "Nucleus", "Where does aerobic respiration release energy?", "Mitochondria"),
                topic("Motion, Distance and Acceleration", "Analyse motion using distance, displacement, speed, velocity and acceleration.", "Describing Motion Quantitatively", "Calculate average speed; distinguish distance and displacement; interpret a simple velocity-time graph.", "Distance is the total path travelled; displacement is the directed change from start to finish. Average speed is distance divided by time, while average velocity uses displacement. Acceleration is change in velocity per unit time. Graphs show how these quantities vary and require units on both axes.", "A cyclist covers 120 m in 20 s, so average speed is 6 m/s. If velocity changes from 2 m/s to 8 m/s in 3 s, acceleration is 2 m/s².", "Speed is scalar and velocity has direction. A flat velocity-time graph means constant velocity; its slope represents acceleration.", "Find speed for 120 m in 20 s.", "6 m/s", "Find acceleration from 2 to 8 m/s in 3 s.", "2 m/s²", "What does the slope of a velocity-time graph show?", "Acceleration", "Can displacement be zero after a journey?", "Yes, if the object returns to its start"));
            case ENGLISH -> List.of(
                topic("Analytical Reading and Argument", "Evaluate a claim, evidence and reasoning in an informational text or short argument.", "How Arguments Are Built", "Separate claim from evidence; identify an assumption; judge whether evidence supports a conclusion.", "An argument presents a claim and reasons for accepting it. Evidence may be data, examples or expert findings; reasoning explains the connection. A strong reader checks source reliability, relevance and limitations. One example may illustrate a claim but may not prove a general rule.", "Claim: “The library should open earlier.” Evidence: attendance records show many students arrive before class. Reasoning connects access with study time; a survey could reveal whether the change meets student needs.", "Do not confuse a claim with evidence. Ask who collected information, when, and whether another explanation is possible.", "What is a claim?", "A position or conclusion", "What links evidence to a claim?", "Reasoning", "Does one example always prove a general rule?", "No", "Name one check for source reliability.", "Who produced the information"),
                topic("Reported Speech and Formal Writing", "Transform direct speech accurately and select an appropriate register for formal communication.", "Reporting Ideas Precisely", "Identify reporting verbs; adjust pronouns and tense where needed; write a concise formal request.", "Reported speech communicates another person's words without quotation marks. Pronouns and time expressions may change to fit the new speaker and context; tense often shifts when the reporting verb is in the past. Formal writing uses a clear purpose, respectful tone and organised information.", "Direct: Ravi said, “I am ready.” Reported: Ravi said that he was ready. A formal email also needs a useful subject line and a clear request.", "Keep the original meaning when changing speech. Do not change tense automatically when a fact remains true or the context does not require it.", "How is direct speech marked?", "Quotation marks", "Change “I am ready” after Ravi said to what?", "He was ready", "What should a formal email subject convey?", "Its purpose", "What must reported speech preserve?", "The speaker's meaning"));
            case SOCIAL_SCIENCE -> List.of(
                topic("Democracy, Elections and Representation", "Examine representation, elections and accountability in a constitutional democracy.", "How Citizens Choose Representatives", "Explain universal adult franchise; describe election stages; connect representation to accountability.", "In a representative democracy, citizens elect people to make public decisions on their behalf. Elections require eligible voters, competing candidates, a secret ballot and fair counting. Representatives remain accountable through public scrutiny, institutions, debate and later elections. Participation also includes informed discussion between elections.", "A voter compares candidate statements with public needs, casts a private ballot and can later evaluate whether promises and responsibilities were addressed.", "A fair election needs clear rules and equal opportunity. A majority decision remains subject to constitutional rights and law.", "What does representation mean?", "Elected people act on behalf of citizens", "Why is a ballot secret?", "To protect free choice", "Name one way representatives are accountable.", "Public scrutiny", "Can majority rule ignore constitutional rights?", "No"),
                topic("Climate, Monsoon and Human Adaptation", "Connect seasonal winds and rainfall patterns with agriculture, water planning and disaster preparedness.", "Understanding Monsoon Variability", "Distinguish weather from climate; explain seasonal monsoon winds; discuss risk reduction.", "Weather describes short-term atmospheric conditions; climate summarises patterns over many years. Monsoon winds change direction seasonally and bring much of India's rainfall, but amount and timing vary by region and year. Communities adapt through reservoirs, crop choices, forecasts and preparedness for floods or droughts.", "A delayed onset can affect sowing dates, so farmers and planners use rainfall observations and forecasts while avoiding assumptions that every monsoon behaves alike.", "Climate patterns are not identical to daily weather. Adaptation reduces risk but cannot prevent every hazard.", "Which describes long-term patterns?", "Climate", "Why does monsoon timing matter?", "It affects water and sowing", "Name one drought adaptation.", "Water storage", "Does every region receive equal rainfall?", "No"));
            case TAMIL -> List.of(
                topic("தமிழ் இலக்கணம்: வேற்றுமை உருபுகள்", "பெயர்ச்சொல்லுடன் சேரும் வேற்றுமை உருபுகள் வாக்கிய உறவுகளை எவ்வாறு காட்டுகின்றன என்பதை அறிதல்.", "சொற்களுக்கிடையிலான உறவு", "வேற்றுமை உருபுகளை அடையாளம் காணுதல்; எழுவாய், செயப்படுபொருள், கருவி உறவுகளைப் புரிதல்.", "பெயர்ச்சொல்லுடன் சேரும் வேற்றுமை உருபுகள் வாக்கியத்தில் அதன் பங்கையும் பிற சொற்களுடனான உறவையும் காட்டுகின்றன. உருபு மாறும்போது வாக்கியத்தின் பொருள் மாறலாம். பொருள், உருபு, வினை ஆகியவற்றை இணைத்துப் படிப்பது தெளிவான இலக்கணப் புரிதலைத் தரும்.", "“மாணவன் பேனாவால் எழுதினான்” என்பதில் “பேனாவால்” கருவியைக் குறிக்கிறது. “பேனாவை எடுத்தான்” என்பதில் உருபு செயப்படுபொருளைச் சுட்டுகிறது.", "உருபை மட்டும் மனப்பாடம் செய்யாமல் வாக்கியத்தில் அது காட்டும் பொருள் உறவை விளக்குங்கள்.", "“பேனாவால்” எதைக் குறிக்கிறது?", "கருவி", "“புத்தகத்தை வாசித்தான்” என்பதில் செயப்படுபொருள் எது?", "புத்தகத்தை", "வேற்றுமை உருபு எதைக் காட்டும்?", "சொற்களுக்கிடையிலான உறவு", "உருபு மாறினால் என்ன மாறலாம்?", "வாக்கியப் பொருள்"),
                topic("தமிழ் இலக்கியம்: செய்யுள் கருத்தாய்வு", "செய்யுளின் மைய உணர்வு, சொல் தேர்வு மற்றும் காட்சிப்படிமங்களை ஆதாரத்துடன் விளக்குதல்.", "கவிதையைச் சான்றுடன் வாசித்தல்", "மைய உணர்வைக் கூறுதல்; சொல் தேர்வின் விளைவை ஆராய்தல்; வரிகளிலிருந்து ஆதாரம் காட்டுதல்.", "செய்யுள் குறைந்த சொற்களில் உணர்வையும் கருத்தையும் செறிவாக வெளிப்படுத்தும். காட்சிப்படிமம் வாசகரின் மனதில் ஒரு காட்சியை உருவாக்கும்; ஒலி அமைப்பு கவிதையின் ஓட்டத்தை வலுப்படுத்தலாம். விளக்கம் உரையிலுள்ள சொற்கள் மற்றும் வரிகளால் ஆதரிக்கப்பட வேண்டும்.", "மழையை “மண்ணின் புன்னகை” என்று கூறும் வரி, பயிருக்கும் நிலத்திற்கும் மழை தரும் புத்துயிரை உருவகமாக உணர்த்துகிறது.", "முதலில் நேரடியான பொருளை வாசிக்கவும்; பின்னர் உருவாகும் உணர்வையும் காட்சியையும் வரிச் சான்றுடன் எழுதவும்.", "கவிதையின் மைய உணர்வு எது?", "முக்கிய உணர்வு அல்லது கருத்து", "காட்சிப்படிமம் எதை உருவாக்கும்?", "மனக்காட்சி", "கவிதை விளக்கம் எதனால் ஆதரிக்கப்பட வேண்டும்?", "வரிச் சான்று", "சொல் தேர்வு எதை வலுப்படுத்தும்?", "உணர்வு மற்றும் கருத்து"));
            default -> throw new IllegalArgumentException("Unsupported class 9 subject: " + s);
        };
    }
}
