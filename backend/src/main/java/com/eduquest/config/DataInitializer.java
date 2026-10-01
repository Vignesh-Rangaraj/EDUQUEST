package com.eduquest.config;

import com.eduquest.domain.*;
import com.eduquest.domain.Module;
import com.eduquest.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    @Value("${app.curriculum.cleanup-legacy:false}")
    private boolean cleanupLegacyCurriculum;

    private static final List<String> DEMO_MODULE_TITLES = Arrays.asList(
            "Fractions & Decimals",
            "Algebra Foundations",
            "Geometry Essentials",
            "Human Body Systems",
            "Solar System & Space",
            "Plants & Environment",
            "Vocabulary & Word Power",
            "Grammar & Sentence Structure",
            "Indian Geography",
            "Indian History & Freedom Movement",
            "Fractions Module",
            "Solar System",
            "Algebraic Expressions"
    );

    private final SchoolRepository schoolRepository;
    private final ClassroomRepository classroomRepository;
    private final UserAccountRepository userAccountRepository;
    private final TeacherRepository teacherRepository;
    private final ParentRepository parentRepository;
    private final StudentRepository studentRepository;
    private final ModuleRepository moduleRepository;
    private final ActivityRepository activityRepository;
    private final LessonContentRepository lessonContentRepository;
    private final QuizQuestionRepository quizQuestionRepository;
    private final GameConfigurationRepository gameConfigRepository;
    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    private final SamacheerCurriculumSeeder samacheerCurriculumSeeder;

    public DataInitializer(
            SchoolRepository schoolRepository,
            ClassroomRepository classroomRepository,
            UserAccountRepository userAccountRepository,
            TeacherRepository teacherRepository,
            ParentRepository parentRepository,
            StudentRepository studentRepository,
            ModuleRepository moduleRepository,
            ActivityRepository activityRepository,
            LessonContentRepository lessonContentRepository,
            QuizQuestionRepository quizQuestionRepository,
            GameConfigurationRepository gameConfigRepository,
            PasswordEncoder passwordEncoder,
            JdbcTemplate jdbcTemplate,
            SamacheerCurriculumSeeder samacheerCurriculumSeeder) {
        this.schoolRepository = schoolRepository;
        this.classroomRepository = classroomRepository;
        this.userAccountRepository = userAccountRepository;
        this.teacherRepository = teacherRepository;
        this.parentRepository = parentRepository;
        this.studentRepository = studentRepository;
        this.moduleRepository = moduleRepository;
        this.activityRepository = activityRepository;
        this.lessonContentRepository = lessonContentRepository;
        this.quizQuestionRepository = quizQuestionRepository;
        this.gameConfigRepository = gameConfigRepository;
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.samacheerCurriculumSeeder = samacheerCurriculumSeeder;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            jdbcTemplate.execute("ALTER TABLE activities DROP CONSTRAINT IF EXISTS activities_activity_type_check");
            jdbcTemplate.execute("ALTER TABLE activities DROP CONSTRAINT IF EXISTS activities_subject_check");
            jdbcTemplate.execute("ALTER TABLE modules DROP CONSTRAINT IF EXISTS modules_subject_check");
        } catch (Exception e) {
            log.warn("Could not drop DB constraints: {}", e.getMessage());
        }

        if (schoolRepository.count() == 0) {
            log.info("Seeding Initial Users & Classrooms...");

            // 1. Create School
            School school = schoolRepository.save(School.builder()
                    .name("EduQuest Demo School")
                    .code("EQ-DEMO-01")
                    .build());

            // 2. Create Classrooms (6-A, 7-A, 8-A, 9-A)
            Classroom class6a = classroomRepository.save(Classroom.builder().grade(6).section("A").name("6-A").school(school).build());
            Classroom class7a = classroomRepository.save(Classroom.builder().grade(7).section("A").name("7-A").school(school).build());
            Classroom class8a = classroomRepository.save(Classroom.builder().grade(8).section("A").name("8-A").school(school).build());
            Classroom class9a = classroomRepository.save(Classroom.builder().grade(9).section("A").name("9-A").school(school).build());

            // 3. Create Super Admin
            userAccountRepository.save(UserAccount.builder()
                    .username("admin")
                    .password(passwordEncoder.encode("admin123"))
                    .fullName("System Administrator")
                    .role(Role.SUPER_ADMIN)
                    .build());

            // 4. Create Teachers
            Teacher t6 = createTeacherAccount("teacher_6a", "password123", "Anita Sharma", class6a);
            Teacher t7 = createTeacherAccount("teacher_7a", "password123", "Karthikeyan R", class7a);
            Teacher t8 = createTeacherAccount("teacher_8a", "password123", "Senthil Kumar", class8a);
            Teacher t9 = createTeacherAccount("teacher_9a", "password123", "Priya Ramesh", class9a);

            // 5. Seed Students & Parents
            String[][] class6aData = {
                    {"student_6a_1", "Arjun Kumar", "parent_6a_1", "Lakshmi Arjun"},
                    {"student_6a_2", "Harini S", "parent_6a_2", "Meena Harini"},
                    {"student_6a_3", "Pranav K", "parent_6a_3", "Kavitha Pranav"},
                    {"student_6a_4", "Nivetha R", "parent_6a_4", "Rameshwari Nivetha"},
                    {"student_6a_5", "Surya Prakash", "parent_6a_5", "Kalaivani Surya"}
            };
            seedClassroomStudentsAndParents(class6a, class6aData);

            String[][] class7aData = {
                    {"student_7a_1", "Kavin Raj", "parent_7a_1", "Revathi Kavin"},
                    {"student_7a_2", "Keerthana M", "parent_7a_2", "Shanthi Keerthana"},
                    {"student_7a_3", "Vishnu V", "parent_7a_3", "Gomathi Vishnu"},
                    {"student_7a_4", "Aishwarya P", "parent_7a_4", "Rajalakshmi Aishwarya"},
                    {"student_7a_5", "Dharshan S", "parent_7a_5", "Selvi Dharshan"}
            };
            seedClassroomStudentsAndParents(class7a, class7aData);

            String[][] class8aData = {
                    {"student_8a_1", "Gokul Krishna", "parent_8a_1", "Lakshmi Gokul"},
                    {"student_8a_2", "Nandhini S", "parent_8a_2", "Revathi Nandhini"},
                    {"student_8a_3", "Vignesh R", "parent_8a_3", "Kalaivani Vignesh"},
                    {"student_8a_4", "Deepika M", "parent_8a_4", "Selvi Deepika"},
                    {"student_8a_5", "Harish Kumar", "parent_8a_5", "Meenakshi Harish"}
            };
            seedClassroomStudentsAndParents(class8a, class8aData);

            String[][] class9aData = {
                    {"student_9a_1", "Akash P", "parent_9a_1", "Rajalakshmi Akash"},
                    {"student_9a_2", "Aarthi K", "parent_9a_2", "Shanthi Aarthi"},
                    {"student_9a_3", "Sanjay R", "parent_9a_3", "Gomathi Sanjay"},
                    {"student_9a_4", "Swetha M", "parent_9a_4", "Uma Swetha"},
                    {"student_9a_5", "Dinesh Kumar", "parent_9a_5", "Kala Dinesh"}
            };
            seedClassroomStudentsAndParents(class9a, class9aData);
        }

        if (cleanupLegacyCurriculum) {
            log.warn("Legacy curriculum cleanup is enabled; modules outside the Samacheer allowlist will be removed.");
            cleanDemoCurriculumIfPresent();
        } else {
            log.info("Skipping legacy curriculum cleanup; set app.curriculum.cleanup-legacy=true to opt in.");
        }

        log.info("Triggering Samacheer Kalvi Curriculum Seeder...");
        samacheerCurriculumSeeder.seed();

        log.info("Initialization Complete! Current Modules: {}, Current Activities: {}",
                moduleRepository.count(), activityRepository.count());
    }

    private static final List<String> LEGACY_SAMACHEER_MODULE_TITLES = Arrays.asList(
            "Numbers and Fractions",
            "The Living World",
            "Vocabulary Building",
            "Ancient Civilizations",
            "Integers and Operations",
            "Nutrition in Plants",
            "Grammar Fundamentals",
            "Indian Geography",
            "Algebra Basics",
            "Force and Pressure",
            "Reading and Vocabulary",
            "Indian Freedom Movement",
            "Polynomials",
            "Cell Biology",
            "Communication Skills",
            "Indian Constitution"
    );

    private static final List<String> SAMACHEER_MODULE_TITLES = java.util.stream.Stream
            .concat(LEGACY_SAMACHEER_MODULE_TITLES.stream(), SamacheerCurriculumSeeder.seededModuleTitles().stream())
            .distinct()
            .collect(Collectors.toList());

    @Transactional
    public void cleanDemoCurriculumIfPresent() {
        List<Module> allModules = moduleRepository.findAll();
        List<Module> modulesToClean = allModules.stream()
                .filter(m -> !SAMACHEER_MODULE_TITLES.contains(m.getTitle()))
                .collect(Collectors.toList());

        for (Module mod : modulesToClean) {
            List<Activity> activities = activityRepository.findByModuleIdOrderByDisplayOrderAsc(mod.getId());
            for (Activity act : activities) {
                try {
                    jdbcTemplate.execute("DELETE FROM student_progress WHERE activity_id = " + act.getId());
                } catch (Exception ignored) {}
                gameConfigRepository.findByActivityId(act.getId()).ifPresent(gameConfigRepository::delete);
                quizQuestionRepository.findByActivityIdOrderByDisplayOrderAsc(act.getId()).forEach(quizQuestionRepository::delete);
                lessonContentRepository.findByActivityId(act.getId()).ifPresent(lessonContentRepository::delete);
                activityRepository.delete(act);
            }
            moduleRepository.delete(mod);
        }

        // Clean orphaned activities
        activityRepository.findAll().forEach(act -> {
            if (act.getModuleId() == null || !moduleRepository.existsById(act.getModuleId())) {
                try {
                    jdbcTemplate.execute("DELETE FROM student_progress WHERE activity_id = " + act.getId());
                } catch (Exception ignored) {}
                gameConfigRepository.findByActivityId(act.getId()).ifPresent(gameConfigRepository::delete);
                quizQuestionRepository.findByActivityIdOrderByDisplayOrderAsc(act.getId()).forEach(quizQuestionRepository::delete);
                lessonContentRepository.findByActivityId(act.getId()).ifPresent(lessonContentRepository::delete);
                activityRepository.delete(act);
            }
        });

        log.info("Cleaned legacy demo curriculum data! Remaining Modules: {}, Activities: {}",
                moduleRepository.count(), activityRepository.count());
    }

    @Transactional
    public void seedGameEngineVerificationSuite() {
        Module existing = moduleRepository.findAll().stream()
                .filter(m -> "Game Engine Verification Suite".equalsIgnoreCase(m.getTitle()))
                .findFirst().orElse(null);

        Teacher teacher = teacherRepository.findByUserAccountUsername("teacher_6a").orElse(null);
        Classroom classroom = classroomRepository.findByName("6-A").orElse(null);

        Long teacherId = teacher != null ? teacher.getId() : null;
        Long classroomId = classroom != null ? classroom.getId() : null;

        if (existing == null) {
            existing = moduleRepository.save(Module.builder()
                    .title("Game Engine Verification Suite")
                    .description("Verification module containing 8 playable game engine tests")
                    .subject(Subject.SCIENCE)
                    .difficultyLevel(DifficultyLevel.BEGINNER)
                    .estimatedMinutes(40)
                    .classroomId(classroomId)
                    .status(ActivityStatus.PUBLISHED)
                    .createdByTeacherId(teacherId)
                    .build());
            log.info("Created Module: Game Engine Verification Suite (ID: {})", existing.getId());
        }

        Long moduleId = existing.getId();
        List<Activity> existingActivities = activityRepository.findByModuleIdOrderByDisplayOrderAsc(moduleId);

        Object[][] gamesData = {
            {
                "Human Body Match",
                ActivityType.MATCH_THE_FOLLOWING,
                1,
                "{\"gameType\":\"MATCH_THE_FOLLOWING\",\"instructions\":\"Match each organ with its primary function:\",\"questions\":[{\"id\":\"1\",\"prompt\":\"Heart\",\"correctAnswer\":\"Pumps Blood\",\"options\":[\"Pumps Blood\",\"Breathing\",\"Controls Body\",\"Digestion\"]},{\"id\":\"2\",\"prompt\":\"Lungs\",\"correctAnswer\":\"Breathing\",\"options\":[\"Pumps Blood\",\"Breathing\",\"Controls Body\",\"Digestion\"]},{\"id\":\"3\",\"prompt\":\"Brain\",\"correctAnswer\":\"Controls Body\",\"options\":[\"Pumps Blood\",\"Breathing\",\"Controls Body\",\"Digestion\"]},{\"id\":\"4\",\"prompt\":\"Stomach\",\"correctAnswer\":\"Digestion\",\"options\":[\"Pumps Blood\",\"Breathing\",\"Controls Body\",\"Digestion\"]}]}"
            },
            {
                "Science Facts",
                ActivityType.TRUE_FALSE,
                2,
                "{\"gameType\":\"TRUE_FALSE\",\"instructions\":\"Determine if each science statement is True or False:\",\"questions\":[{\"id\":\"1\",\"prompt\":\"The Sun is a star.\",\"correctAnswer\":\"True\",\"options\":[\"True\",\"False\"]},{\"id\":\"2\",\"prompt\":\"Humans can breathe underwater without equipment.\",\"correctAnswer\":\"False\",\"options\":[\"True\",\"False\"]},{\"id\":\"3\",\"prompt\":\"Plants produce oxygen during photosynthesis.\",\"correctAnswer\":\"True\",\"options\":[\"True\",\"False\"]},{\"id\":\"4\",\"prompt\":\"Earth has two moons.\",\"correctAnswer\":\"False\",\"options\":[\"True\",\"False\"]}]}"
            },
            {
                "Basic Science Fill Ups",
                ActivityType.FILL_IN_THE_BLANK,
                3,
                "{\"gameType\":\"FILL_IN_THE_BLANK\",\"instructions\":\"Fill in the blanks to complete the science statements:\",\"questions\":[{\"id\":\"1\",\"prompt\":\"Plants make their own _____.\",\"correctAnswer\":\"food\"},{\"id\":\"2\",\"prompt\":\"The Earth revolves around the _____.\",\"correctAnswer\":\"sun\"},{\"id\":\"3\",\"prompt\":\"Water freezes at _____ degrees Celsius.\",\"correctAnswer\":\"0\"}]}"
            },
            {
                "Solar System Flashcards",
                ActivityType.FLASH_CARDS,
                4,
                "{\"gameType\":\"FLASH_CARDS\",\"instructions\":\"Review the Solar System flashcards and select the correct description:\",\"questions\":[{\"id\":\"1\",\"prompt\":\"Mercury\",\"correctAnswer\":\"Closest planet to the Sun\",\"options\":[\"Closest planet to the Sun\",\"Hottest planet in the solar system\",\"Our home planet\",\"The Red Planet\"]},{\"id\":\"2\",\"prompt\":\"Venus\",\"correctAnswer\":\"Hottest planet in the solar system\",\"options\":[\"Closest planet to the Sun\",\"Hottest planet in the solar system\",\"Our home planet\",\"The Red Planet\"]},{\"id\":\"3\",\"prompt\":\"Earth\",\"correctAnswer\":\"Our home planet\",\"options\":[\"Closest planet to the Sun\",\"Hottest planet in the solar system\",\"Our home planet\",\"The Red Planet\"]},{\"id\":\"4\",\"prompt\":\"Mars\",\"correctAnswer\":\"The Red Planet\",\"options\":[\"Closest planet to the Sun\",\"Hottest planet in the solar system\",\"Our home planet\",\"The Red Planet\"]}]}"
            },
            {
                "Science Scramble",
                ActivityType.WORD_SCRAMBLE,
                5,
                "{\"gameType\":\"WORD_SCRAMBLE\",\"instructions\":\"Unscramble the letters to form the correct science term:\",\"questions\":[{\"id\":\"1\",\"prompt\":\"Unscramble: NEGOXY\",\"word\":\"NEGOXY\",\"correctAnswer\":\"OXYGEN\",\"hint\":\"Gas essential for breathing\"},{\"id\":\"2\",\"prompt\":\"Unscramble: TANELP\",\"word\":\"TANELP\",\"correctAnswer\":\"PLANET\",\"hint\":\"Celestial body orbiting a star\"},{\"id\":\"3\",\"prompt\":\"Unscramble: YTIVARG\",\"word\":\"YTIVARG\",\"correctAnswer\":\"GRAVITY\",\"hint\":\"Force pulling objects down\"},{\"id\":\"4\",\"prompt\":\"Unscramble: CLLE\",\"word\":\"CLLE\",\"correctAnswer\":\"CELL\",\"hint\":\"Basic unit of life\"}]}"
            },
            {
                "Math Shooter",
                ActivityType.SHOOT_THE_ANSWER,
                6,
                "{\"gameType\":\"SHOOT_THE_ANSWER\",\"instructions\":\"Aim and shoot the target with the correct answer:\",\"questions\":[{\"prompt\":\"What is 25% of 100?\",\"correctAnswer\":\"25\",\"options\":[\"10\",\"20\",\"25\",\"50\"]},{\"prompt\":\"Solve: 8 x 7\",\"correctAnswer\":\"56\",\"options\":[\"54\",\"56\",\"64\",\"48\"]},{\"prompt\":\"What is the square root of 64?\",\"correctAnswer\":\"8\",\"options\":[\"6\",\"7\",\"8\",\"9\"]}]}"
            },
            {
                "States Of Matter",
                ActivityType.BALLOON_POP,
                7,
                "{\"gameType\":\"BALLOON_POP\",\"instructions\":\"Pop the floating balloon that contains the correct answer:\",\"questions\":[{\"prompt\":\"Which of the following is a Gas?\",\"correctAnswer\":\"Oxygen\",\"options\":[\"Water\",\"Ice\",\"Oxygen\",\"Iron\"]},{\"prompt\":\"Which of the following is a Liquid?\",\"correctAnswer\":\"Water\",\"options\":[\"Steam\",\"Water\",\"Rock\",\"Oxygen\"]},{\"prompt\":\"Which of the following is a Solid?\",\"correctAnswer\":\"Iron\",\"options\":[\"Oxygen\",\"Water\",\"Iron\",\"Air\"]}]}"
            },
            {
                "Solar System Quest",
                ActivityType.TREASURE_HUNT,
                8,
                "{\"gameType\":\"TREASURE_HUNT\",\"instructions\":\"Follow the clues across space to find the golden treasure chest!\",\"stages\":[{\"stageIndex\":1,\"clue\":\"📜 Clue #1: Find the blue planet with liquid oceans\",\"question\":\"Which planet is known as the Water Planet?\",\"options\":[\"Venus\",\"Earth\",\"Mars\",\"Jupiter\"],\"correctAnswer\":\"Earth\"},{\"stageIndex\":2,\"clue\":\"🗝️ Clue #2: Navigate to the star at the center of our solar system\",\"question\":\"What is the primary source of light for Earth?\",\"options\":[\"Moon\",\"Sun\",\"Alpha Centauri\",\"Polaris\"],\"correctAnswer\":\"Sun\"},{\"stageIndex\":3,\"clue\":\"🏆 Final Stage: Explore the Red Planet with dusty surface and ancient volcanoes\",\"question\":\"Which planet is target for future human landings?\",\"options\":[\"Mercury\",\"Venus\",\"Mars\",\"Saturn\"],\"correctAnswer\":\"Mars\"}]}"
            }
        };

        for (Object[] row : gamesData) {
            String title = (String) row[0];
            ActivityType type = (ActivityType) row[1];
            int displayOrder = (Integer) row[2];
            String configJson = (String) row[3];

            boolean exists = existingActivities.stream().anyMatch(a -> title.equalsIgnoreCase(a.getTitle()));
            if (!exists) {
                Activity activity = activityRepository.save(Activity.builder()
                        .title(title)
                        .description("Verification game for engine type: " + type.name())
                        .subject(Subject.SCIENCE)
                        .activityType(type)
                        .status(ActivityStatus.PUBLISHED)
                        .moduleId(moduleId)
                        .displayOrder(displayOrder)
                        .xpReward(50)
                        .visibleToStudents(true)
                        .createdByTeacherId(teacherId)
                        .assignedClassroomId(classroomId)
                        .build());

                gameConfigRepository.save(GameConfiguration.builder()
                        .activityId(activity.getId())
                        .jsonConfiguration(configJson)
                        .build());

                log.info("Seeded Game Activity: {} (Type: {}, ID: {})", title, type, activity.getId());
            }
        }
    }

    @Transactional
    public void seedDemoCurriculumOnDemand() {
        Teacher teacher = teacherRepository.findByUserAccountUsername("teacher_6a").orElse(null);
        Classroom classroom = classroomRepository.findByName("6-A").orElse(null);

        if (teacher == null || classroom == null) return;

        Module demoMod = moduleRepository.save(Module.builder()
                .title("Fractions & Decimals")
                .description("Demo module on proper fractions, mixed numbers, and decimals.")
                .subject(Subject.MATHEMATICS)
                .difficultyLevel(DifficultyLevel.BEGINNER)
                .estimatedMinutes(45)
                .classroomId(classroom.getId())
                .status(ActivityStatus.PUBLISHED)
                .createdByTeacherId(teacher.getId())
                .build());

        Activity demoLesson = activityRepository.save(Activity.builder()
                .title("Fractions Basics Lesson")
                .description("Introduction to proper, improper, and mixed fractions.")
                .subject(Subject.MATHEMATICS)
                .activityType(ActivityType.LESSON)
                .status(ActivityStatus.PUBLISHED)
                .moduleId(demoMod.getId())
                .displayOrder(1)
                .xpReward(10)
                .visibleToStudents(true)
                .createdByTeacherId(teacher.getId())
                .assignedClassroomId(classroom.getId())
                .build());

        lessonContentRepository.save(LessonContent.builder()
                .activityId(demoLesson.getId())
                .content("<h1>Fractions Basics</h1><p>A fraction represents a part of a whole.</p>")
                .estimatedMinutes(10)
                .build());

        log.info("Demo curriculum loaded on demand for teacher: {}", teacher.getUserAccount().getFullName());
    }

    private Teacher createTeacherAccount(String username, String password, String fullName, Classroom classroom) {
        UserAccount user = userAccountRepository.save(UserAccount.builder()
                .username(username)
                .password(passwordEncoder.encode(password))
                .fullName(fullName)
                .role(Role.SUPER_ADMIN.equals(username) ? Role.SUPER_ADMIN : Role.TEACHER)
                .build());

        return teacherRepository.save(Teacher.builder()
                .userAccount(user)
                .classroom(classroom)
                .build());
    }

    private void seedClassroomStudentsAndParents(Classroom classroom, String[][] data) {
        for (String[] row : data) {
            String studentUsername = row[0];
            String studentName = row[1];
            String parentUsername = row[2];
            String parentName = row[3];

            UserAccount pUser = userAccountRepository.save(UserAccount.builder()
                    .username(parentUsername)
                    .password(passwordEncoder.encode("password123"))
                    .fullName(parentName)
                    .role(Role.PARENT)
                    .build());

            Parent parent = parentRepository.save(Parent.builder()
                    .userAccount(pUser)
                    .build());

            UserAccount sUser = userAccountRepository.save(UserAccount.builder()
                    .username(studentUsername)
                    .password(passwordEncoder.encode("password123"))
                    .fullName(studentName)
                    .role(Role.STUDENT)
                    .build());

            studentRepository.save(Student.builder()
                    .userAccount(sUser)
                    .classroom(classroom)
                    .parent(parent)
                    .xp(0)
                    .level(1)
                    .coins(0)
                    .currentStreak(0)
                    .build());
        }
    }
}
