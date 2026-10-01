import os
import asyncio
from playwright.async_api import async_playwright

SVG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\figure_6_1_workflow.svg"
PNG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\figure_6_1_workflow.png"

svg_content = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 1560" width="1000" height="1560" style="background-color: #ffffff; font-family: 'Times New Roman', Times, serif;">
  <defs>
    <!-- Solid Black Downward Arrow Marker -->
    <marker id="arrow-down" viewBox="0 0 10 10" refX="5" refY="8" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 1 0 L 5 10 L 9 0 z" fill="#000000" />
    </marker>
  </defs>

  <style>
    .figure-title { font-size: 24px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .box-title { font-size: 15px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .box-subtitle { font-size: 13px; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .terminator-text { font-size: 16px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .connector-line { stroke: #000000; stroke-width: 2; fill: none; }
  </style>

  <!-- Figure Title -->
  <text x="500" y="45" class="figure-title">Figure 6.1: User and School Management Workflow</text>

  <!-- 1. Start Terminator -->
  <rect x="390" y="80" width="220" height="48" rx="24" ry="24" fill="#f0f0f0" stroke="#000000" stroke-width="2" />
  <text x="500" y="110" class="terminator-text">Start</text>

  <line x1="500" y1="128" x2="500" y2="173" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 2. Super Admin Login -->
  <rect x="230" y="173" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="206" class="box-title">Super Admin Login</text>

  <line x1="500" y1="228" x2="500" y2="273" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 3. Manage Schools -->
  <rect x="230" y="273" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="300" class="box-title">Manage Schools</text>
  <text x="500" y="322" class="box-subtitle">(Create School / Update School / Delete School)</text>

  <line x1="500" y1="338" x2="500" y2="383" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 4. Create School Admin -->
  <rect x="230" y="383" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="416" class="box-title">Create School Admin</text>

  <line x1="500" y1="438" x2="500" y2="483" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 5. School Admin Login -->
  <rect x="230" y="483" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="516" class="box-title">School Admin Login</text>

  <line x1="500" y1="538" x2="500" y2="583" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 6. Manage Teachers -->
  <rect x="230" y="583" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="610" class="box-title">Manage Teachers</text>
  <text x="500" y="632" class="box-subtitle">(Create Teacher Accounts)</text>

  <line x1="500" y1="648" x2="500" y2="693" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 7. Manage Students -->
  <rect x="230" y="693" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="720" class="box-title">Manage Students</text>
  <text x="500" y="742" class="box-subtitle">(Create Student Accounts / Import Student Data)</text>

  <line x1="500" y1="758" x2="500" y2="803" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 8. Assign Teachers to Classes -->
  <rect x="230" y="803" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="836" class="box-title">Assign Teachers to Classes</text>

  <line x1="500" y1="858" x2="500" y2="903" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 9. Assign Students to Classes -->
  <rect x="230" y="903" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="936" class="box-title">Assign Students to Classes</text>

  <line x1="500" y1="958" x2="500" y2="1003" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 10. Store User Information in Database -->
  <rect x="230" y="1003" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1036" class="box-title">Store User Information in Database</text>

  <line x1="500" y1="1058" x2="500" y2="1103" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 11. Role-Based Access Control Validation -->
  <rect x="230" y="1103" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1136" class="box-title">Role-Based Access Control Validation</text>

  <line x1="500" y1="1158" x2="500" y2="1203" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 12. User Dashboard Access -->
  <rect x="230" y="1203" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1230" class="box-title">User Dashboard Access</text>
  <text x="500" y="1252" class="box-subtitle">(Super Admin / School Admin / Teacher / Student)</text>

  <line x1="500" y1="1268" x2="500" y2="1313" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 13. User and School Management Completed -->
  <rect x="230" y="1313" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1346" class="box-title">User and School Management Completed</text>

  <line x1="500" y1="1368" x2="500" y2="1413" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 14. End Terminator -->
  <rect x="390" y="1413" width="220" height="48" rx="24" ry="24" fill="#f0f0f0" stroke="#000000" stroke-width="2" />
  <text x="500" y="1443" class="terminator-text">End</text>
</svg>"""

with open(SVG_OUTPUT_PATH, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"SVG generated successfully: {SVG_OUTPUT_PATH}")

async def render_svg_to_png():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(device_scale_factor=2)
        await page.set_viewport_size({"width": 1000, "height": 1560})
        html_wrapper = f"""<!DOCTYPE html>
<html>
<head>
<style>
  body {{ margin: 0; padding: 0; background: white; width: 1000px; height: 1560px; overflow: hidden; }}
  svg {{ width: 1000px; height: 1560px; display: block; }}
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
