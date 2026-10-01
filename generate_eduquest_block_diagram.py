import os
import asyncio
from playwright.async_api import async_playwright

SVG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_block_diagram.svg"
PNG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_block_diagram.png"

svg_content = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1800 1000" width="1800" height="1000" style="background-color: #ffffff; font-family: 'Times New Roman', Times, serif;">
  <defs>
    <!-- Arrowhead Markers -->
    <marker id="arrow-right" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 1 L 10 5 L 0 9 z" fill="#000000" />
    </marker>
    <marker id="arrow-down" viewBox="0 0 10 10" refX="5" refY="8" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 1 0 L 5 10 L 9 0 z" fill="#000000" />
    </marker>
    <marker id="arrow-left" viewBox="0 0 10 10" refX="2" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 10 1 L 0 5 L 10 9 z" fill="#000000" />
    </marker>
  </defs>

  <style>
    .diagram-title { font-size: 26px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .block-header-text { font-size: 14px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .sub-item-text { font-size: 12px; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .sub-item-bold { font-size: 12px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .caption-text { font-size: 13.5px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .flow-connector { stroke: #000000; stroke-width: 2; fill: none; }
  </style>

  <!-- Title -->
  <text x="900" y="45" class="diagram-title">Block Diagram of EduQuest</text>

  <!-- ==================== ROW 1 (BLOCKS 1 TO 5) ==================== -->

  <!-- Block 1: User Registration & Login -->
  <g id="block-1" transform="translate(50, 90)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">1. User Registration &amp; Login</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">Student Registration &amp; Login</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Teacher Auth &amp; Profiles</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Admin Authentication</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Role-Based Access Control (RBAC)</text>
    </g>
  </g>

  <!-- Arrow 1 -> 2 -->
  <line x1="360" y1="260" x2="395" y2="260" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 2: School & Classroom Management -->
  <g id="block-2" transform="translate(395, 90)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">2. School &amp; Class Management</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">School Creation &amp; Config</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Class &amp; Grade Setup</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Teacher Class Assignment</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Student Roster Enrolment</text>
    </g>
  </g>

  <!-- Arrow 2 -> 3 -->
  <line x1="705" y1="260" x2="740" y2="260" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 3: Lesson Management -->
  <g id="block-3" transform="translate(740, 90)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">3. Lesson Management</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">Create Lessons &amp; Subjects</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Publish Content Workflow</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Module Organization</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Samacheer Kalvi Alignment</text>
    </g>
  </g>

  <!-- Arrow 3 -> 4 -->
  <line x1="1050" y1="260" x2="1085" y2="260" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 4: Quiz & Assessment -->
  <g id="block-4" transform="translate(1085, 90)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">4. Quiz &amp; Assessment</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">MCQ &amp; Item Creation</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Quiz Publishing Engine</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Attempt &amp; Response Tracking</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Automated Score Validation</text>
    </g>
  </g>

  <!-- Arrow 4 -> 5 -->
  <line x1="1395" y1="260" x2="1430" y2="260" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 5: Gamification Engine -->
  <g id="block-5" transform="translate(1430, 90)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">5. Gamification Engine</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">XP Calculation Engine</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Coins &amp; Reward System</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Badge &amp; Trophy Allocation</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Progress &amp; Streak Tracking</text>
    </g>
  </g>

  <!-- Row 1 to Row 2 Snake Connector Arrow (Block 5 -> Block 6) -->
  <path d="M 1585 430 L 1585 475 L 205 475 L 205 520" fill="none" stroke="#000000" stroke-width="2" marker-end="url(#arrow-down)" />

  <!-- ==================== ROW 2 (BLOCKS 6 TO 10) ==================== -->

  <!-- Block 6: AI Learning Assistant -->
  <g id="block-6" transform="translate(50, 520)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">6. AI Learning Assistant</text>

    <g transform="translate(15, 50)">
      <rect x="0" y="0" width="135" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="67.5" y="25" class="sub-item-bold">Explain Mode</text>

      <rect x="145" y="0" width="135" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="212.5" y="25" class="sub-item-bold">Hint Mode</text>

      <rect x="0" y="52" width="135" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="67.5" y="77" class="sub-item-bold">Example Mode</text>

      <rect x="145" y="52" width="135" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="212.5" y="77" class="sub-item-bold">Simplify Mode</text>

      <rect x="0" y="104" width="280" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="129" class="sub-item-bold">Practice Mode</text>

      <rect x="0" y="156" width="280" height="112" fill="#f8f8f8" stroke="#000000" stroke-width="1" />
      <text x="140" y="195" class="sub-item-bold">Gemini API Integration</text>
      <text x="140" y="225" class="sub-item-text">Real-time Hints &amp; Summaries</text>
    </g>
  </g>

  <!-- Arrow 6 -> 7 -->
  <line x1="360" y1="690" x2="395" y2="690" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 7: Offline Learning & Synchronization -->
  <g id="block-7" transform="translate(395, 520)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">7. Offline Learning &amp; Sync</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#e8e8e8" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">IndexedDB Storage</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Offline Activity Capture</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Unsynced Payload Queue</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Automatic PostgreSQL Sync</text>
    </g>
  </g>

  <!-- Arrow 7 -> 8 -->
  <line x1="705" y1="690" x2="740" y2="690" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 8: Analytics Processing -->
  <g id="block-8" transform="translate(740, 520)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">8. Analytics Processing</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">Student Performance Analysis</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Engagement Tracking</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Mastery Metrics Engine</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Learning Progress Reports</text>
    </g>
  </g>

  <!-- Arrow 8 -> 9 -->
  <line x1="1050" y1="690" x2="1085" y2="690" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 9: Dashboard Generation -->
  <g id="block-9" transform="translate(1085, 520)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">9. Dashboard Generation</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">Student Dashboard</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">Teacher Dashboard</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Admin Dashboard</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">Real-Time Data Feeds</text>
    </g>
  </g>

  <!-- Arrow 9 -> 10 -->
  <line x1="1395" y1="690" x2="1430" y2="690" class="flow-connector" marker-end="url(#arrow-right)" />

  <!-- Block 10: Reports & Output -->
  <g id="block-10" transform="translate(1430, 520)">
    <rect x="0" y="0" width="310" height="340" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="0" y="0" width="310" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="155" y="24" class="block-header-text">10. Reports &amp; Output</text>

    <g transform="translate(15, 52)">
      <rect x="0" y="0" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="34" class="sub-item-bold">Quiz Results &amp; Scorecards</text>

      <rect x="0" y="70" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="104" class="sub-item-bold">XP &amp; Badge Reports</text>

      <rect x="0" y="140" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="174" class="sub-item-bold">Learning Analytics Reports</text>

      <rect x="0" y="210" width="280" height="58" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="244" class="sub-item-bold">System Telemetry Output</text>
    </g>
  </g>

  <!-- Bottom Caption -->
  <rect x="50" y="905" width="1690" height="42" fill="#f8f8f8" stroke="#000000" stroke-width="1.5" />
  <text x="895" y="931" class="caption-text">User Authentication &#8594; School Management &#8594; Lesson Creation &#8594; Quiz Assessment &#8594; Gamification &#8594; AI Assistance &#8594; Offline Sync &#8594; Analytics &#8594; Dashboard Generation &#8594; Reports &amp; Output</text>
</svg>"""

with open(SVG_OUTPUT_PATH, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"SVG generated successfully: {SVG_OUTPUT_PATH}")

async def render_svg_to_png():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(device_scale_factor=2)
        await page.set_viewport_size({"width": 1800, "height": 1000})
        html_wrapper = f"""<!DOCTYPE html>
<html>
<head>
<style>
  body {{ margin: 0; padding: 0; background: white; width: 1800px; height: 1000px; overflow: hidden; }}
  svg {{ width: 1800px; height: 1000px; display: block; }}
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
