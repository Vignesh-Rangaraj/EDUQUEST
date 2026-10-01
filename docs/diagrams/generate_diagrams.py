from __future__ import annotations

"""Generate manuscript figures as paired editable SVG and presentation PNG files."""

from pathlib import Path
from xml.sax.saxutils import escape
from PIL import Image, ImageDraw, ImageFont

OUT = Path(__file__).parent
FONT = Path(r"C:\Windows\Fonts\arial.ttf")
FONT_BOLD = Path(r"C:\Windows\Fonts\arialbd.ttf")
NAVY = "#17324D"
INK = "#24364B"
MUTED = "#5D7188"
BLUE = "#2563EB"
PALE_BLUE = "#EAF2FF"
TEAL = "#078B83"
PALE_TEAL = "#E7F7F4"
AMBER = "#D98200"
PALE_AMBER = "#FFF4DD"
PURPLE = "#7353B7"
PALE_PURPLE = "#F2EDFB"
GREEN = "#18834F"
PALE_GREEN = "#EAF7EF"
RED = "#B64242"
PALE_RED = "#FFF0EF"
GRAY = "#E5EBF1"
WHITE = "#FFFFFF"


class Figure:
    def __init__(self, name: str, width: int, height: int, title: str, subtitle: str):
        self.name, self.width, self.height = name, width, height
        self.title, self.subtitle = title, subtitle
        self.items: list[tuple] = []

    def rect(self, x, y, w, h, fill=WHITE, stroke=GRAY, radius=16, dashed=False, sw=2):
        self.items.append(("rect", x, y, w, h, fill, stroke, radius, dashed, sw))

    def text(self, x, y, value, size=20, color=INK, bold=False, anchor="start"):
        self.items.append(("text", x, y, value, size, color, bold, anchor))

    def line(self, x1, y1, x2, y2, color=MUTED, sw=3, arrow=True, dashed=False):
        self.items.append(("line", x1, y1, x2, y2, color, sw, arrow, dashed))

    def card(self, x, y, w, h, title, lines, fill=WHITE, accent=BLUE, title_size=21, body_size=17, dashed=False):
        self.rect(x, y, w, h, fill, accent, 16, dashed)
        self.rect(x, y, 8, h, accent, accent, 4, dashed)
        self.text(x + 24, y + 35, title, title_size, NAVY, True)
        yy = y + 66
        for line in lines:
            self.text(x + 24, yy, line, body_size, MUTED)
            yy += body_size + 9

    def label(self, x, y, value, fill, color=INK, size=16):
        w = max(96, len(value) * size * .58 + 26)
        self.rect(x, y, w, 34, fill, fill, 16, sw=1)
        self.text(x + w/2, y + 23, value, size, color, True, "middle")

    def save(self):
        # Shared drawing model keeps vector and raster editions visually consistent.
        svg = [f'<svg xmlns="http://www.w3.org/2000/svg" width="{self.width}" height="{self.height}" viewBox="0 0 {self.width} {self.height}">',
               '<defs><marker id="arrow" markerWidth="10" markerHeight="10" refX="8" refY="3" orient="auto"><path d="M0,0 L0,6 L9,3 z" fill="context-stroke"/></marker></defs>',
               f'<rect width="100%" height="100%" fill="{WHITE}"/>',
               f'<text x="48" y="58" font-family="Arial" font-size="34" font-weight="700" fill="{NAVY}">{escape(self.title)}</text>',
               f'<text x="48" y="91" font-family="Arial" font-size="18" fill="{MUTED}">{escape(self.subtitle)}</text>']
        img = Image.new("RGB", (self.width, self.height), WHITE)
        d = ImageDraw.Draw(img)
        d.text((48, 24), self.title, font=ImageFont.truetype(str(FONT_BOLD), 34), fill=NAVY)
        d.text((48, 68), self.subtitle, font=ImageFont.truetype(str(FONT), 18), fill=MUTED)
        for it in self.items:
            if it[0] == "rect":
                _, x,y,w,h,fill,stroke,r,dashed,sw = it
                svg.append(f'<rect x="{x}" y="{y}" width="{w}" height="{h}" rx="{r}" fill="{fill}" stroke="{stroke}" stroke-width="{sw}"'+(' stroke-dasharray="9 7"' if dashed else '')+'/>')
                if dashed:
                    # PNG dashes are represented by the solid accent rail; SVG remains fully dashed.
                    d.rounded_rectangle((x,y,x+w,y+h),radius=r,fill=fill,outline=stroke,width=sw)
                else: d.rounded_rectangle((x,y,x+w,y+h),radius=r,fill=fill,outline=stroke,width=sw)
            elif it[0] == "text":
                _,x,y,val,size,color,bold,anchor = it
                svg.append(f'<text x="{x}" y="{y}" font-family="Arial, sans-serif" font-size="{size}" font-weight="{700 if bold else 400}" fill="{color}" text-anchor="{anchor}">{escape(val)}</text>')
                fnt = ImageFont.truetype(str(FONT_BOLD if bold else FONT), size)
                if anchor == "middle": d.text((x,y-size*.78),val,font=fnt,fill=color,anchor="mt")
                else: d.text((x,y-size*.78),val,font=fnt,fill=color)
            elif it[0] == "line":
                _,x1,y1,x2,y2,color,sw,arrow,dashed = it
                svg.append(f'<line x1="{x1}" y1="{y1}" x2="{x2}" y2="{y2}" stroke="{color}" stroke-width="{sw}"'+(' stroke-dasharray="8 7"' if dashed else '')+(' marker-end="url(#arrow)"' if arrow else '')+'/>')
                d.line((x1,y1,x2,y2),fill=color,width=sw)
                if arrow:
                    import math
                    a=math.atan2(y2-y1,x2-x1); L=13
                    p1=(x2-L*math.cos(a-.48), y2-L*math.sin(a-.48)); p2=(x2-L*math.cos(a+.48),y2-L*math.sin(a+.48))
                    d.polygon([(x2,y2),p1,p2],fill=color)
        svg.append('</svg>')
        (OUT / f"{self.name}.svg").write_text("\n".join(svg), encoding="utf-8")
        img.save(OUT / f"{self.name}.png", dpi=(220,220), optimize=True)


def architecture():
    f=Figure("fig1_system_architecture",1800,1120,"EduQuest: Overall System Architecture","Six cooperating software layers with local-first activity execution and deferred server reconciliation")
    # Horizontal layers keep cross-layer flow readable at manuscript scale.
    f.card(70,135,500,130,"1  Client application",["React 18 + TypeScript single-page app","Student, teacher, parent and admin portals"],PALE_BLUE,BLUE)
    f.card(650,135,500,130,"2  Offline storage",["IndexedDB through Dexie.js","Student, activity and progress repositories"],PALE_TEAL,TEAL)
    f.card(1230,135,500,130,"3  Synchronization",["Network status monitor + client sync service","Queue manager and retryable queue items"],PALE_AMBER,AMBER)
    f.card(1230,415,500,130,"4  Transport",["Axios HTTP client","Batched JSON synchronization requests"],PALE_PURPLE,PURPLE)
    f.card(650,415,500,160,"5  Backend services",["Spring Boot 3 / Java 17 REST APIs","Sync controller + sync service","XP and streak services; JWT / RBAC"],PALE_BLUE,BLUE)
    f.card(70,415,500,160,"6  Persistence",["PostgreSQL production database","H2 local development and test database","Relational learning and sync records"],PALE_GREEN,GREEN)
    # flows
    f.line(570,200,650,200,BLUE)
    f.line(1150,200,1230,200,TEAL)
    f.line(1480,265,1480,415,AMBER)
    f.line(1230,480,1150,480,PURPLE)
    f.line(650,495,570,495,BLUE)
    f.text(860,340,"local reads / writes",16,TEAL,True)
    f.text(1210,375,"online transition",16,AMBER,True)
    # external service
    f.card(650,690,500,110,"External AI provider",["Google Gemini API (AI Tutor requests; online)"],PALE_PURPLE,PURPLE)
    f.line(900,575,900,690,PURPLE)
    f.text(930,638,"AI prompt / response",16,PURPLE,True)
    f.rect(70,865,1660,135,"#F7F9FC",GRAY,14,sw=1)
    f.text(95,905,"Execution principle",19,NAVY,True)
    f.text(95,942,"Learning reads and progress writes can complete against local storage. Connectivity enables queued synchronization and server confirmation.",18,INK)
    f.text(95,975,"AI calls require network access; authentication and role checks protect backend endpoints.",17,MUTED)
    f.save()


def sync_sequence():
    f=Figure("fig2_offline_sync_sequence",1900,1240,"Offline Activity Synchronization Sequence","A completion is persisted locally first; server reconciliation is retried using the same client queue-item identifier")
    names=["Student / UI","Local activity +\nrepositories","Dexie queue","Network + sync\nservice","Authenticated sync\nendpoint","Sync service +\nrelational DB"]
    xs=[150,450,750,1050,1380,1710]
    for x,n in zip(xs,names):
        f.rect(x-115,130,230,90,PALE_BLUE,BLUE,14)
        for j,s in enumerate(n.split("\n")): f.text(x,164+j*24,s,17,NAVY,True,"middle")
        f.line(x,220,x,1100,GRAY,2,False,True)
    def msg(a,b,y,label,color=BLUE,ret=False):
        f.line(xs[a],y,xs[b],y,color,2,not ret,ret)
        f.text((xs[a]+xs[b])/2,y-10,label,15,INK,True,"middle")
    msg(0,1,280,"1  Complete activity; compute outcome locally")
    msg(1,2,365,"2  Save progress + XP transaction")
    msg(2,1,450,"3  Persist queue item (UUID, action, payload)",TEAL,True)
    msg(1,0,535,"4  Show saved result immediately",TEAL,True)
    msg(3,3,620,"5  Online event drains due PENDING items",AMBER)
    msg(3,4,705,"6  Send authenticated batch; mark items PROCESSING",PURPLE)
    msg(4,5,790,"7  Validate student ownership; process per item",PURPLE)
    msg(5,5,875,"8  Check client item ID in sync_queue")
    f.rect(1460,905,405,68,PALE_GREEN,GREEN,12)
    f.text(1662,933,"Known ID: return success (no reapply)",15,GREEN,True,"middle")
    f.rect(1460,984,405,68,PALE_BLUE,BLUE,12)
    f.text(1662,1012,"New ID: apply progress / XP, record ID",15,BLUE,True,"middle")
    msg(5,3,1085,"9  Return per-item results (partial batches supported)",GREEN,True)
    f.rect(100,1140,1640,64,"#F7F9FC",GRAY,12,sw=1)
    f.text(125,1179,"10  Client removes confirmed items; failures remain retryable with backoff and the original identifier.",17,NAVY,True)
    f.save()


def erd():
    f=Figure("fig3_core_erd",2100,1460,"Core EduQuest Entity Relationship Diagram","Fourteen entities in the manuscript's core schema; connector labels show the documented one-to-many or one-to-one links")
    # card placement grouped by school/content and learner state
    cards={
      "School":(70,150,300,112,["id (PK)","name, code"]),
      "Classroom":(470,150,300,112,["id (PK)","school_id (FK)","grade, section"]),
      "Teacher":(870,150,300,112,["id (PK)","user_account_id", "classroom_id (FK)"]),
      "Parent":(1490,150,300,112,["id (PK)","user_account_id"]),
      "Student":(1490,390,300,150,["id (PK)","user_account_id", "classroom_id / parent_id", "xp, level, streaks"]),
      "Module":(470,390,300,112,["id (PK)","classroom_id", "created_by_teacher_id"]),
      "Activity":(470,630,300,150,["id (PK)","module_id", "activity_type, status", "xp_reward"]),
      "LessonContent":(70,900,300,112,["id (PK)","activity_id (FK)", "content"]),
      "GameConfiguration":(470,900,300,112,["id (PK)","activity_id (FK)", "configuration_json"]),
      "QuizQuestion":(870,900,300,112,["id (PK)","activity_id (FK)", "question / answer data"]),
      "StudentProgress":(1250,630,300,112,["id (PK)","student_id, activity_id", "score, completed"]),
      "StudentXPTransaction":(1640,630,360,112,["id (PK)","student_id, activity_id", "xp_awarded; append-only"]),
      "StudentBadge":(1250,900,300,112,["id (PK)","student_id", "badge_code, earned_at"]),
      "SyncQueue":(1640,900,360,135,["id (PK)","client_queue_item_id", "student_id, action, status"]),
    }
    for name,(x,y,w,h,fields) in cards.items():
        f.rect(x,y,w,h,WHITE,BLUE if name in ("Module","Activity","LessonContent","QuizQuestion","GameConfiguration") else TEAL,12)
        f.rect(x,y,w,34,PALE_BLUE if name in ("Module","Activity","LessonContent","QuizQuestion","GameConfiguration") else PALE_TEAL,PALE_BLUE,12,sw=0)
        f.text(x+12,y+25,name,17,NAVY,True)
        for i,field in enumerate(fields): f.text(x+12,y+58+i*22,field,14,MUTED)
    # connectors, line+label; logical IDs are noted below where source model uses scalar IDs.
    def rel(x1,y1,x2,y2,label,tx,ty,color=MUTED):
        f.line(x1,y1,x2,y2,color,2,True)
        f.text(tx,ty,label,13,color,True)
    rel(370,205,470,205,"1 : N",390,190)
    rel(770,180,870,180,"1 : N",790,165)
    f.line(770,225,800,225,MUTED,2,False)
    f.line(800,225,800,450,MUTED,2,False)
    rel(800,450,1490,450,"1 : N",1120,435)
    rel(620,262,620,390,"1 : N",640,330)
    rel(1020,262,620,390,"1 : N",790,325)
    rel(1640,262,1640,390,"1 : N",1660,332)
    rel(620,502,620,630,"1 : N",640,565)
    rel(770,690,1250,690,"1 : N",990,675)
    rel(770,750,1640,742,"1 : N",1120,770)
    rel(620,780,220,900,"1 : 0..1",385,840)
    rel(620,780,620,900,"1 : 0..1",640,845)
    rel(720,760,950,900,"1 : N",790,842)
    rel(1640,540,1400,630,"1 : N",1480,590)
    rel(1640,540,1640,630,"1 : N",1660,582)
    # Route badge and queue links around the progress / XP entity cards.
    f.line(1790,500,2040,500,MUTED,2,False)
    f.line(2040,500,2040,860,MUTED,2,False)
    f.line(2040,860,1400,860,MUTED,2,False)
    f.line(1400,860,1400,900,MUTED,2,True)
    f.text(1430,850,"1 : N",13,MUTED,True)
    f.line(2040,860,1820,860,MUTED,2,False)
    f.line(1820,860,1820,900,MUTED,2,True)
    f.text(1850,850,"1 : N",13,MUTED,True)
    f.rect(70,1110,1930,235,"#F7F9FC",GRAY,14,sw=1)
    f.text(95,1150,"Relationship and scope notes",19,NAVY,True)
    f.text(95,1190,"School → Classroom → Teacher / Student / Module → Activity → lesson, game, quiz and progress records.",16,INK)
    f.text(95,1225,"Student → progress, XP ledger, badges and sync queue; Parent → Student. Lines follow the manuscript's stated relationships.",16,INK)
    f.text(95,1260,"UserAccount identifiers are shown as scalar references and omitted as an additional entity to preserve the 14-entity scope.",15,MUTED)
    f.text(95,1300,"Some associations (e.g., module_id, activity_id) are represented by scalar IDs in the current backend model; connector lines indicate logical links.",15,RED)
    f.save()


def gamification():
    f=Figure("fig4_gamification_framework",1800,1100,"Gamification and Student Progression Framework","Teacher-configurable activity formats feed a shared XP, streak, badge and learning-journey model")
    f.card(70,160,330,145,"Teacher-authored activity",["Choose format and configure content","Common GameConfiguration"],PALE_BLUE,BLUE)
    formats=["Match the Following","True or False","Fill in the Blank","Flash Cards","Word Scramble","Shoot the Answer","Balloon Pop","Treasure Hunt"]
    for i,name in enumerate(formats):
        col=i%4; row=i//4; x=450+col*300; y=145+row*112
        f.rect(x,y,270,76,WHITE,TEAL,14)
        f.text(x+135,y+47,name,16,NAVY,True,"middle")
    f.line(400,230,430,230,BLUE)
    f.line(430,230,430,190,BLUE,2,False)
    f.line(430,190,450,190,BLUE)
    f.line(430,190,430,300,BLUE,2,False)
    f.line(430,300,450,300,BLUE)
    f.card(70,540,400,150,"Activity result",["Completion + score","Local-first progress record","Reward event (when eligible)"],PALE_TEAL,TEAL)
    f.card(650,520,440,170,"Progress and reward data",["XP ledger / accumulated XP","Streak fields + badge records","Stored locally; synchronized later"],PALE_AMBER,AMBER)
    f.card(1270,450,440,145,"Progression indicators",["Level = floor(XP / 100) + 1","Displayed level grows each 100 XP"],PALE_BLUE,BLUE)
    f.card(1270,625,440,145,"Streak continuity",["EffectiveStreak = max(current, highest)","Retain the recorded high-water value"],PALE_RED,RED,body_size=16)
    f.card(1270,800,440,145,"Learning Journey",["Village → Farm → Forest → River","→ Mountain → Castle; milestone unlocks"],PALE_GREEN,GREEN,body_size=16)
    f.line(470,615,650,615,TEAL)
    f.line(1090,560,1270,520,AMBER)
    f.line(1090,605,1270,695,AMBER)
    f.line(1090,650,1270,870,AMBER)
    f.rect(70,985,1640,72,"#F7F9FC",GRAY,14,sw=1)
    f.text(95,1028,"Eight formats support different activity styles. The formulas shown are those stated in the manuscript.",17,NAVY,True)
    f.save()


def ai_workflow():
    f=Figure("fig5_ai_assistant_workflow",1900,1120,"EduQuest AI Tutor Workflow","Source-verified implementation: authenticated, lesson-aware tutoring with browser-native voice support")
    f.card(70,190,310,165,"Student",["Text chat or supported voice input","Choose language and tutor mode"],PALE_BLUE,BLUE)
    f.card(500,150,370,235,"AI Tutor (verified)",["CHAT · EXPLAIN · SIMPLIFY","HINT · PRACTICE","Optional lesson context","English, Tamil, Hindi, Malayalam,","Telugu and Kannada"],PALE_TEAL,TEAL,body_size=16)
    f.card(1010,190,340,165,"Backend AI service",["Authenticated request + validation","Build language / lesson prompt","Persist chat messages"],PALE_PURPLE,PURPLE)
    f.card(1500,190,320,165,"Google Gemini API",["Backend-only API key","Network-dependent generation"],PALE_AMBER,AMBER)
    f.line(380,270,500,270,BLUE)
    f.line(870,270,1010,270,TEAL)
    f.line(1350,270,1500,270,PURPLE)
    f.line(1500,320,1350,320,PURPLE,2,True,True)
    f.line(1010,340,870,340,TEAL,2,True,True)
    f.line(500,340,380,340,BLUE,2,True,True)
    f.card(500,505,370,140,"Chat history",["chat_sessions + chat_messages","Load and continue prior sessions"],PALE_BLUE,BLUE)
    f.line(680,385,680,505,BLUE)
    f.card(1010,505,340,140,"Lesson context (optional)",["Activity title / description","LessonContent, when lessonId is sent"],PALE_GREEN,GREEN,body_size=16)
    f.line(1180,385,1180,505,GREEN)
    f.card(70,505,310,140,"Voice controls (browser)",["Speech recognition: en / ta / hi","Speech synthesis follows language"],PALE_AMBER,AMBER,body_size=16)
    f.line(220,355,220,505,AMBER)
    f.rect(70,760,1750,170,"#F7F9FC",GRAY,14,sw=1)
    f.text(95,802,"Verified implementation scope",18,NAVY,True)
    f.card(95,830,390,75,"Five Tutor modes · six languages",["CHAT, EXPLAIN, SIMPLIFY, HINT, PRACTICE"],PALE_BLUE,BLUE,17,14)
    f.card(520,830,390,75,"Lesson context · chat history",["Optional Activity / LessonContent context; saved sessions"],PALE_TEAL,TEAL,17,14)
    f.card(1010,830,760,75,"Browser voice · online generation",["Speech input: English, Tamil, Hindi. Speech output uses browser synthesis. New AI responses require internet."],PALE_AMBER,AMBER,17,14)
    f.text(95,985,"PRACTICE requests may ask for a few practice questions; they are not a separate teacher quiz-authoring workflow.",15,MUTED)
    f.save()


def ai_workflow_ieee():
    """Compact, high-label-density version intended for a two-column IEEE page."""
    f=Figure("fig5_ai_assistant_workflow_ieee",1000,1320,"Implemented AI Tutor","Authenticated request → lesson-aware Gemini response → saved conversation")
    f.card(60,135,880,190,"Student request",["CHAT · EXPLAIN · SIMPLIFY · HINT · PRACTICE","Response languages: English · Tamil · Hindi","Malayalam · Telugu · Kannada"],PALE_BLUE,BLUE,28,24)
    f.line(500,325,500,360,BLUE,3)
    f.card(60,370,880,270,"Backend Tutor service",["Validate message, mode and language","Optional lessonId adds the Activity title and description","plus associated LessonContent","Build the prompt with selected language and lesson context"],PALE_TEAL,TEAL,28,24)
    f.line(500,640,500,675,TEAL,3)
    f.card(60,685,880,175,"Google Gemini API",["Called from the backend using server-side configuration","Network access is required for new AI responses"],PALE_AMBER,AMBER,28,24)
    f.line(500,860,500,895,AMBER,3)
    f.card(60,905,880,170,"Persist and return",["Save user and AI messages in the chat session","Return the answer to the student interface"],PALE_PURPLE,PURPLE,28,24)
    f.rect(60,1100,880,185,PALE_GREEN,GREEN,14)
    f.text(82,1142,"Voice and offline behavior",27,GREEN,True)
    f.text(82,1185,"Speech input: English, Tamil and Hindi. Browser speech synthesis",23,MUTED)
    f.text(82,1220,"reads replies. New AI calls need internet; the UI reports unavailability",23,MUTED)
    f.text(82,1255,"and disables voice while offline.",23,MUTED)
    f.save()


if __name__ == "__main__":
    OUT.mkdir(parents=True, exist_ok=True)
    architecture(); sync_sequence(); erd(); gamification(); ai_workflow(); ai_workflow_ieee()
    print("Generated 5 manuscript figures (plus compact IEEE Fig. 5 variant) as SVG + PNG in", OUT)
