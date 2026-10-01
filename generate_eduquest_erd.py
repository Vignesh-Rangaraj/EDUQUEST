import os
import asyncio
from playwright.async_api import async_playwright

SVG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_erd_diagram.svg"
PNG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_erd_diagram.png"

# Helper function to generate table XML in SVG
def draw_entity_table(x, y, width, title, fields):
    row_height = 24
    header_height = 32
    total_height = header_height + len(fields) * row_height
    
    svg = []
    # Outer box
    svg.append(f'<rect x="{x}" y="{y}" width="{width}" height="{total_height}" fill="#ffffff" stroke="#000000" stroke-width="2" />')
    # Header box
    svg.append(f'<rect x="{x}" y="{y}" width="{width}" height="{header_height}" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />')
    svg.append(f'<text x="{x + width/2}" y="{y + 21}" class="entity-title">{title}</text>')
    
    # Rows
    for i, (key_type, field_name) in enumerate(fields):
        ry = y + header_height + i * row_height
        # Row line divider
        if i > 0:
            svg.append(f'<line x1="{x}" y1="{ry}" x2="{x + width}" y2="{ry}" stroke="#000000" stroke-width="0.8" />')
        
        # Key column divider
        key_col_w = 42
        svg.append(f'<line x1="{x + key_col_w}" y1="{ry}" x2="{x + key_col_w}" y2="{ry + row_height}" stroke="#000000" stroke-width="0.8" />')
        
        # Key text
        if key_type == 'PK':
            svg.append(f'<text x="{x + key_col_w/2}" y="{ry + 16}" class="pk-text">PK</text>')
        elif key_type == 'FK':
            svg.append(f'<text x="{x + key_col_w/2}" y="{ry + 16}" class="fk-text">FK</text>')
            
        # Field text
        font_weight = "bold" if key_type in ['PK', 'FK'] else "normal"
        svg.append(f'<text x="{x + key_col_w + 10}" y="{ry + 16}" class="field-text" style="font-weight: {font_weight};">{field_name}</text>')

    return "\n".join(svg), total_height

# Coordinates and entity schema definitions
entities = {
    "USERS": {
        "x": 380, "y": 100, "w": 300,
        "fields": [
            ("PK", "id"),
            ("", "name"),
            ("", "email"),
            ("", "password"),
            ("", "role"),
            ("", "is_active"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "SCHOOLS": {
        "x": 1320, "y": 100, "w": 300,
        "fields": [
            ("PK", "id"),
            ("", "school_name"),
            ("", "district"),
            ("", "address"),
            ("", "contact_number"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "TEACHERS": {
        "x": 380, "y": 440, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "school_id"),
            ("FK", "user_id"),
            ("", "employee_id"),
            ("", "qualification"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "STUDENTS": {
        "x": 1320, "y": 440, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "school_id"),
            ("FK", "user_id"),
            ("", "roll_number"),
            ("", "grade"),
            ("", "section"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "LESSONS": {
        "x": 80, "y": 780, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "teacher_id"),
            ("", "title"),
            ("", "description"),
            ("", "subject"),
            ("", "grade_level"),
            ("", "status"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "MODULES": {
        "x": 80, "y": 1120, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "lesson_id"),
            ("", "module_title"),
            ("", "module_order"),
            ("", "content_type"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "QUIZZES": {
        "x": 80, "y": 1420, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "module_id"),
            ("", "title"),
            ("", "passing_score"),
            ("", "xp_reward"),
            ("", "created_at"),
            ("", "updated_at")
        ]
    },
    "QUESTIONS": {
        "x": 80, "y": 1720, "w": 300,
        "fields": [
            ("PK", "id"),
            ("FK", "quiz_id"),
            ("", "question_text"),
            ("", "option_a"),
            ("", "option_b"),
            ("", "option_c"),
            ("", "option_d"),
            ("", "correct_answer"),
            ("", "created_at")
        ]
    },
    "STUDENT_QUIZ_ATTEMPTS": {
        "x": 680, "y": 1120, "w": 320,
        "fields": [
            ("PK", "id"),
            ("FK", "student_id"),
            ("FK", "quiz_id"),
            ("", "score"),
            ("", "attempt_number"),
            ("", "is_passed"),
            ("", "submitted_at")
        ]
    },
    "XP_RECORDS": {
        "x": 1150, "y": 920, "w": 290,
        "fields": [
            ("PK", "id"),
            ("FK", "student_id"),
            ("", "source_type"),
            ("", "xp_earned"),
            ("", "created_at")
        ]
    },
    "BADGES": {
        "x": 1600, "y": 920, "w": 290,
        "fields": [
            ("PK", "id"),
            ("", "badge_name"),
            ("", "description"),
            ("", "xp_requirement"),
            ("", "created_at")
        ]
    },
    "STUDENT_BADGES": {
        "x": 1380, "y": 1180, "w": 290,
        "fields": [
            ("PK", "id"),
            ("FK", "student_id"),
            ("FK", "badge_id"),
            ("", "earned_at")
        ]
    },
    "AI_CHAT_HISTORY": {
        "x": 1150, "y": 1480, "w": 290,
        "fields": [
            ("PK", "id"),
            ("FK", "student_id"),
            ("", "question"),
            ("", "response"),
            ("", "mode"),
            ("", "created_at")
        ]
    },
    "ANALYTICS": {
        "x": 1600, "y": 1480, "w": 290,
        "fields": [
            ("PK", "id"),
            ("FK", "student_id"),
            ("", "lesson_progress"),
            ("", "quiz_completion"),
            ("", "engagement_score"),
            ("", "last_activity")
        ]
    }
}

# Generate SVG strings for entities
entity_svg_list = []
entity_heights = {}
for name, data in entities.items():
    svg_str, height = draw_entity_table(data["x"], data["y"], data["w"], name, data["fields"])
    entity_svg_list.append(svg_str)
    entity_heights[name] = height

# Relationship lines with labels
def draw_relationship(path_d, label_parent_pos, label_child_pos, parent_card="1", child_card="N"):
    svg = []
    svg.append(f'<path d="{path_d}" fill="none" stroke="#000000" stroke-width="1.8" />')
    
    # Parent cardinality badge
    px, py = label_parent_pos
    svg.append(f'<rect x="{px-10}" y="{py-10}" width="20" height="20" fill="#ffffff" stroke="#000000" stroke-width="1" />')
    svg.append(f'<text x="{px}" y="{py+4}" class="card-text">{parent_card}</text>')
    
    # Child cardinality badge
    cx, cy = label_child_pos
    svg.append(f'<rect x="{cx-10}" y="{cy-10}" width="20" height="20" fill="#ffffff" stroke="#000000" stroke-width="1" />')
    svg.append(f'<text x="{cx}" y="{cy+4}" class="card-text">{child_card}</text>')
    
    return "\n".join(svg)

relationships_svg = [
    # 1. USERS (1) -> TEACHERS (N)
    draw_relationship("M 530 328 L 530 440", (530, 350), (530, 420), "1", "N"),
    
    # 2. USERS (1) -> STUDENTS (N)
    draw_relationship("M 680 230 L 1100 230 L 1100 500 L 1320 500", (705, 230), (1295, 500), "1", "N"),
    
    # 3. SCHOOLS (1) -> TEACHERS (N)
    draw_relationship("M 1320 210 L 900 210 L 900 520 L 680 520", (1295, 210), (705, 520), "1", "N"),
    
    # 4. SCHOOLS (1) -> STUDENTS (N)
    draw_relationship("M 1470 268 L 1470 440", (1470, 290), (1470, 420), "1", "N"),
    
    # 5. TEACHERS (1) -> LESSONS (N)
    draw_relationship("M 380 550 L 230 550 L 230 780", (355, 550), (230, 755), "1", "N"),
    
    # 6. LESSONS (1) -> MODULES (N)
    draw_relationship("M 230 1028 L 230 1120", (230, 1050), (230, 1098), "1", "N"),
    
    # 7. MODULES (1) -> QUIZZES (1)
    draw_relationship("M 230 1296 L 230 1420", (230, 1318), (230, 1398), "1", "1"),
    
    # 8. QUIZZES (1) -> QUESTIONS (N)
    draw_relationship("M 230 1596 L 230 1720", (230, 1618), (230, 1698), "1", "N"),
    
    # 9. STUDENTS (1) -> STUDENT_QUIZ_ATTEMPTS (N)
    draw_relationship("M 1320 570 L 840 570 L 840 1120", (1295, 570), (840, 1098), "1", "N"),
    
    # 10. QUIZZES (1) -> STUDENT_QUIZ_ATTEMPTS (N)
    draw_relationship("M 380 1530 L 530 1530 L 530 1230 L 680 1230", (405, 1530), (655, 1230), "1", "N"),
    
    # 11. STUDENTS (1) -> XP_RECORDS (N)
    draw_relationship("M 1470 676 L 1470 820 L 1295 820 L 1295 920", (1470, 700), (1295, 898), "1", "N"),
    
    # 12. STUDENTS (1) -> STUDENT_BADGES (N)
    draw_relationship("M 1520 676 L 1520 1180", (1520, 700), (1520, 1158), "1", "N"),
    
    # 13. BADGES (1) -> STUDENT_BADGES (N)
    draw_relationship("M 1745 1068 L 1745 1270 L 1670 1270", (1745, 1090), (1695, 1270), "1", "N"),
    
    # 14. STUDENTS (1) -> AI_CHAT_HISTORY (N)
    draw_relationship("M 1370 676 L 1370 1480", (1370, 700), (1370, 1458), "1", "N"),
    
    # 15. STUDENTS (1) -> ANALYTICS (1)
    draw_relationship("M 1600 570 L 1745 570 L 1745 1480", (1625, 570), (1745, 1458), "1", "1")
]

svg_content = f"""<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 2000 2050" width="2000" height="2050" style="background-color: #ffffff; font-family: 'Times New Roman', Times, serif;">
  <style>
    .diagram-title {{ font-size: 28px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }}
    .entity-title {{ font-size: 15px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; letter-spacing: 0.5px; }}
    .pk-text {{ font-size: 11px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }}
    .fk-text {{ font-size: 11px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }}
    .field-text {{ font-size: 12.5px; text-anchor: start; fill: #000000; font-family: 'Times New Roman', serif; }}
    .card-text {{ font-size: 12px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }}
    .legend-title {{ font-size: 13.5px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }}
    .legend-text {{ font-size: 12px; font-weight: bold; fill: #000000; font-family: 'Times New Roman', serif; }}
  </style>

  <!-- Title -->
  <text x="1000" y="48" class="diagram-title">Entity Relationship Diagram of EduQuest</text>

  <!-- Relationships -->
  {"\n".join(relationships_svg)}

  <!-- Entities -->
  {"\n".join(entity_svg_list)}

  <!-- Legend Box -->
  <g id="legend-box" transform="translate(680, 1720)">
    <rect x="0" y="0" width="320" height="150" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="320" height="32" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="160" y="21" class="legend-title">Legend</text>
    
    <text x="35" y="58" class="legend-text">PK = Primary Key</text>
    <text x="35" y="83" class="legend-text">FK = Foreign Key</text>
    <text x="35" y="108" class="legend-text">1  = One</text>
    <text x="35" y="133" class="legend-text">N  = Many</text>
  </g>
</svg>"""

with open(SVG_OUTPUT_PATH, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"SVG generated successfully: {SVG_OUTPUT_PATH}")

async def render_svg_to_png():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(device_scale_factor=2)
        await page.set_viewport_size({"width": 2000, "height": 2050})
        html_wrapper = f"""<!DOCTYPE html>
<html>
<head>
<style>
  body {{ margin: 0; padding: 0; background: white; width: 2000px; height: 2050px; overflow: hidden; }}
  svg {{ width: 2000px; height: 2050px; display: block; }}
</style>
</head>
<body>
{svg_content}
</body>
</html>"""
        await page.set_content(html_wrapper, wait_until="load")
        await page.screenshot(path=PNG_OUTPUT_PATH, full_page=True)
        await browser.close()
        print(f"High-Res PNG rendered successfully: {PNG_OUTPUT_PATH}")

asyncio.run(render_svg_to_png())
