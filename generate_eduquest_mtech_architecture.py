import os
import asyncio
from playwright.async_api import async_playwright

SVG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_system_architecture_mtech.svg"
PNG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\eduquest_system_architecture_mtech.png"

svg_content = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1600 1200" width="1600" height="1200" style="background-color: #ffffff; font-family: 'Times New Roman', Times, serif;">
  <defs>
    <!-- Arrowhead Markers -->
    <marker id="arrow-right" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 1 L 10 5 L 0 9 z" fill="#000000" />
    </marker>
    <marker id="arrow-left" viewBox="0 0 10 10" refX="2" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 10 1 L 0 5 L 10 9 z" fill="#000000" />
    </marker>
    <marker id="arrow-down" viewBox="0 0 10 10" refX="5" refY="8" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 1 0 L 5 10 L 9 0 z" fill="#000000" />
    </marker>
    <marker id="arrow-up" viewBox="0 0 10 10" refX="5" refY="2" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 1 10 L 5 0 L 9 10 z" fill="#000000" />
    </marker>
  </defs>

  <style>
    .diagram-title { font-size: 26px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .layer-header-text { font-size: 15px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; letter-spacing: 0.5px; }
    .box-header-text { font-size: 13px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .item-text { font-size: 12.5px; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .item-text-bold { font-size: 13px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .arrow-label { font-size: 11px; fill: #000000; font-family: 'Times New Roman', serif; text-anchor: middle; }
    .sub-item-text { font-size: 11.5px; text-anchor: middle; fill: #111111; font-family: 'Times New Roman', serif; }
  </style>

  <!-- Title -->
  <text x="800" y="42" class="diagram-title">System Architecture of EduQuest</text>

  <!-- ==================== TOP ROW ==================== -->

  <!-- 1. FRONTEND LAYER -->
  <g id="frontend-layer">
    <rect x="40" y="70" width="360" height="500" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="40" y="70" width="360" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="220" y="94" class="layer-header-text">FRONTEND LAYER</text>

    <!-- Stacked Boxes -->
    <g transform="translate(55, 125)">
      <rect x="0" y="0" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="31" class="item-text-bold">React</text>

      <rect x="0" y="64" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="95" class="item-text-bold">Vite</text>

      <rect x="0" y="128" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="159" class="item-text-bold">Material UI</text>

      <rect x="0" y="192" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="223" class="item-text-bold">React Router DOM</text>

      <rect x="0" y="256" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="287" class="item-text-bold">Axios</text>

      <rect x="0" y="320" width="330" height="52" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="351" class="item-text-bold">IndexedDB (Offline Storage)</text>
    </g>
  </g>

  <!-- 2. BACKEND / API LAYER -->
  <g id="backend-layer">
    <rect x="540" y="70" width="520" height="500" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="540" y="70" width="520" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="800" y="94" class="layer-header-text">BACKEND / API LAYER</text>

    <!-- Top Tech Stack Row -->
    <g transform="translate(555, 122)">
      <rect x="0" y="0" width="155" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="77.5" y="25" class="item-text-bold">Spring Boot</text>

      <rect x="167" y="0" width="155" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="244.5" y="25" class="item-text-bold">REST API</text>

      <rect x="334" y="0" width="156" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="412" y="25" class="item-text-bold">JWT Authentication</text>
    </g>

    <!-- Services Stacked -->
    <g transform="translate(555, 178)">
      <rect x="0" y="0" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="25" class="item-text">User Management</text>

      <rect x="0" y="52" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="77" class="item-text">School Management</text>

      <rect x="0" y="104" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="129" class="item-text">Lesson Management</text>

      <rect x="0" y="156" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="181" class="item-text">Quiz Management</text>

      <rect x="0" y="208" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="233" class="item-text">Gamification Service</text>

      <rect x="0" y="260" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="285" class="item-text">AI Learning Assistant</text>

      <rect x="0" y="312" width="490" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="245" y="337" class="item-text">Analytics Service &amp; Sync Service</text>
    </g>
  </g>

  <!-- 3. DATABASE / STORAGE LAYER -->
  <g id="database-layer">
    <rect x="1200" y="70" width="360" height="500" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="1200" y="70" width="360" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="1380" y="94" class="layer-header-text">DATABASE / STORAGE LAYER</text>

    <!-- PostgreSQL main banner -->
    <g transform="translate(1215, 120)">
      <rect x="0" y="0" width="330" height="36" fill="#f0f0f0" stroke="#000000" stroke-width="1.5" />
      <text x="165" y="23" class="item-text-bold" style="font-size: 14px;">PostgreSQL</text>
    </g>

    <!-- Data Items -->
    <g transform="translate(1215, 164)">
      <rect x="0" y="0" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="21" class="sub-item-text">Users</text>

      <rect x="0" y="42" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="63" class="sub-item-text">Schools &amp; Classrooms</text>

      <rect x="0" y="84" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="105" class="sub-item-text">Teachers &amp; Students</text>

      <rect x="0" y="126" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="147" class="sub-item-text">Lessons &amp; Modules</text>

      <rect x="0" y="168" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="189" class="sub-item-text">Quizzes &amp; Questions</text>

      <rect x="0" y="210" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="231" class="sub-item-text">Student Attempts &amp; Answers</text>

      <rect x="0" y="252" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="273" class="sub-item-text">XP Records &amp; Coins</text>

      <rect x="0" y="294" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="315" class="sub-item-text">Achievements &amp; Badges</text>

      <rect x="0" y="336" width="330" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="165" y="357" class="sub-item-text">Analytics Data &amp; Telemetry</text>
    </g>
  </g>

  <!-- Horizontal Inter-Layer Connectors -->
  <!-- Frontend <-> Backend -->
  <line x1="400" y1="210" x2="540" y2="210" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-right)" />
  <rect x="415" y="194" width="110" height="14" fill="#ffffff" />
  <text x="470" y="205" class="arrow-label">REST API (HTTP)</text>

  <line x1="540" y1="360" x2="400" y2="360" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-left)" />
  <rect x="415" y="364" width="110" height="14" fill="#ffffff" />
  <text x="470" y="375" class="arrow-label">Response (JSON)</text>

  <!-- Backend <-> Database -->
  <line x1="1060" y1="210" x2="1200" y2="210" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-right)" />
  <rect x="1070" y="194" width="120" height="14" fill="#ffffff" />
  <text x="1130" y="205" class="arrow-label">Read / Write (Data)</text>

  <line x1="1200" y1="360" x2="1060" y2="360" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-left)" />
  <rect x="1070" y="364" width="120" height="14" fill="#ffffff" />
  <text x="1130" y="375" class="arrow-label">Database Response</text>

  <!-- ==================== MIDDLE ROW ==================== -->

  <!-- 4. PROCESSING LAYER -->
  <g id="processing-layer">
    <rect x="40" y="620" width="1520" height="260" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="40" y="620" width="1520" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="800" y="644" class="layer-header-text">PROCESSING LAYER</text>

    <!-- 5 Inner Sub-Blocks -->
    <!-- Content Processing -->
    <g transform="translate(55, 672)">
      <rect x="0" y="0" width="280" height="194" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="280" height="30" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="140" y="20" class="box-header-text">Content Processing</text>
      
      <rect x="12" y="42" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="67" class="sub-item-text">Lesson Publishing</text>
      
      <rect x="12" y="92" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="117" class="sub-item-text">Quiz Evaluation</text>
      
      <rect x="12" y="142" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="167" class="sub-item-text">Activity Management</text>
    </g>

    <!-- Gamification Engine -->
    <g transform="translate(355, 672)">
      <rect x="0" y="0" width="280" height="194" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="280" height="30" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="140" y="20" class="box-header-text">Gamification Engine</text>

      <rect x="12" y="38" width="256" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="59" class="sub-item-text">XP Calculation</text>

      <rect x="12" y="76" width="256" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="97" class="sub-item-text">Badge Allocation</text>

      <rect x="12" y="114" width="256" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="135" class="sub-item-text">Progress Tracking</text>

      <rect x="12" y="152" width="256" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="173" class="sub-item-text">Leaderboard Processing</text>
    </g>

    <!-- AI Learning Assistant -->
    <g transform="translate(655, 672)">
      <rect x="0" y="0" width="290" height="194" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="290" height="30" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="145" y="20" class="box-header-text">AI Learning Assistant</text>

      <rect x="10" y="36" width="130" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="75" y="61" class="sub-item-text">Explain Mode</text>

      <rect x="150" y="36" width="130" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="215" y="61" class="sub-item-text">Hint Mode</text>

      <rect x="10" y="86" width="130" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="75" y="111" class="sub-item-text">Example Mode</text>

      <rect x="150" y="86" width="130" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="215" y="111" class="sub-item-text">Simplify Mode</text>

      <rect x="10" y="136" width="270" height="46" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="145" y="164" class="sub-item-text">Practice Mode &amp; Real-time AI Hints</text>
    </g>

    <!-- Offline Sync Engine -->
    <g transform="translate(965, 672)">
      <rect x="0" y="0" width="280" height="194" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="280" height="30" fill="#e0e0e0" stroke="#000000" stroke-width="1" />
      <text x="140" y="20" class="box-header-text">Offline Sync Engine</text>

      <rect x="12" y="42" width="256" height="42" fill="#e8e8e8" stroke="#000000" stroke-width="1" />
      <text x="140" y="67" class="sub-item-text" style="font-weight: bold;">IndexedDB Storage</text>

      <rect x="12" y="92" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="117" class="sub-item-text">Local Payload Queue</text>

      <rect x="12" y="142" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="167" class="sub-item-text">PostgreSQL Synchronization</text>
    </g>

    <!-- Analytics Engine -->
    <g transform="translate(1265, 672)">
      <rect x="0" y="0" width="280" height="194" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="280" height="30" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="140" y="20" class="box-header-text">Analytics Engine</text>

      <rect x="12" y="42" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="67" class="sub-item-text">Performance Analytics</text>

      <rect x="12" y="92" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="117" class="sub-item-text">Engagement Metrics</text>

      <rect x="12" y="142" width="256" height="42" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="140" y="167" class="sub-item-text">Progress Reports</text>
    </g>
  </g>

  <!-- Connectors Backend <-> Processing -->
  <line x1="720" y1="570" x2="720" y2="620" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-down)" />
  <rect x="610" y="587" width="105" height="22" fill="#ffffff" />
  <text x="662" y="602" class="arrow-label" style="font-size: 10.5px;">Process Learning Activities / AI Requests</text>

  <line x1="880" y1="620" x2="880" y2="570" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-up)" />
  <rect x="885" y="587" width="115" height="22" fill="#ffffff" />
  <text x="942" y="602" class="arrow-label" style="font-size: 10.5px;">Generate Analytics / Sync Payload</text>

  <!-- ==================== BOTTOM ROW ==================== -->

  <!-- 5. OUTPUT LAYER -->
  <g id="output-layer">
    <rect x="40" y="930" width="1520" height="220" fill="#ffffff" stroke="#000000" stroke-width="2" />
    <rect x="40" y="930" width="1520" height="38" fill="#e8e8e8" stroke="#000000" stroke-width="1.5" />
    <text x="800" y="954" class="layer-header-text">OUTPUT LAYER</text>

    <!-- 3 Dashboards -->
    <!-- Student Dashboard -->
    <g transform="translate(65, 982)">
      <rect x="0" y="0" width="460" height="152" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="460" height="28" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="230" y="19" class="box-header-text">Student Dashboard</text>

      <rect x="15" y="36" width="205" height="46" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="117.5" y="63" class="sub-item-text">Quiz Results</text>

      <rect x="240" y="36" width="205" height="46" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="342.5" y="63" class="sub-item-text">XP Progress</text>

      <rect x="15" y="92" width="205" height="46" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="117.5" y="119" class="sub-item-text">Achievements</text>

      <rect x="240" y="92" width="205" height="46" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="342.5" y="119" class="sub-item-text">Learning Recommendations</text>
    </g>

    <!-- Teacher Dashboard -->
    <g transform="translate(570, 982)">
      <rect x="0" y="0" width="460" height="152" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="460" height="28" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="230" y="19" class="box-header-text">Teacher Dashboard</text>

      <rect x="15" y="36" width="430" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="230" y="57" class="sub-item-text">Student Performance</text>

      <rect x="15" y="75" width="430" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="230" y="96" class="sub-item-text">Class Analytics</text>

      <rect x="15" y="114" width="430" height="32" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="230" y="135" class="sub-item-text">Activity Reports</text>
    </g>

    <!-- Admin Dashboard -->
    <g transform="translate(1075, 982)">
      <rect x="0" y="0" width="445" height="152" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <rect x="0" y="0" width="445" height="28" fill="#f4f4f4" stroke="#000000" stroke-width="1" />
      <text x="222.5" y="19" class="box-header-text">Admin Dashboard</text>

      <rect x="15" y="36" width="415" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="222.5" y="57" class="sub-item-text">School Statistics</text>

      <rect x="15" y="75" width="415" height="34" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="222.5" y="96" class="sub-item-text">User Reports</text>

      <rect x="15" y="114" width="415" height="32" fill="#ffffff" stroke="#000000" stroke-width="1" />
      <text x="222.5" y="135" class="sub-item-text">System Analytics</text>
    </g>
  </g>

  <!-- Processing -> Output Connector -->
  <line x1="800" y1="880" x2="800" y2="930" stroke="#000000" stroke-width="1.5" marker-end="url(#arrow-down)" />
  <rect x="710" y="898" width="180" height="15" fill="#ffffff" />
  <text x="800" y="909" class="arrow-label">Dashboard Feeds &amp; Visual Output</text>
</svg>"""

with open(SVG_OUTPUT_PATH, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"SVG generated successfully: {SVG_OUTPUT_PATH}")

async def render_svg_to_png():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(device_scale_factor=2)
        await page.set_viewport_size({"width": 1600, "height": 1200})
        html_wrapper = f"""<!DOCTYPE html>
<html>
<head>
<style>
  body {{ margin: 0; padding: 0; background: white; width: 1600px; height: 1200px; overflow: hidden; }}
  svg {{ width: 1600px; height: 1200px; display: block; }}
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
