import os
import asyncio
from playwright.async_api import async_playwright

SVG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\figure_6_2_workflow.svg"
PNG_OUTPUT_PATH = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92\figure_6_2_workflow.png"

svg_content = """<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1000 1680" width="1000" height="1680" style="background-color: #ffffff; font-family: 'Times New Roman', Times, serif;">
  <defs>
    <!-- Solid Black Downward Arrow Marker -->
    <marker id="arrow-down" viewBox="0 0 10 10" refX="5" refY="8" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 1 0 L 5 10 L 9 0 z" fill="#000000" />
    </marker>
    <!-- Solid Black Rightward Arrow Marker -->
    <marker id="arrow-right" viewBox="0 0 10 10" refX="8" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 1 L 10 5 L 0 9 z" fill="#000000" />
    </marker>
  </defs>

  <style>
    .figure-title { font-size: 24px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .box-title { font-size: 15px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .box-subtitle { font-size: 13px; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .diamond-text { font-size: 14px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .terminator-text { font-size: 16px; font-weight: bold; text-anchor: middle; fill: #000000; font-family: 'Times New Roman', serif; }
    .connector-line { stroke: #000000; stroke-width: 2; fill: none; }
    .branch-label { font-size: 13px; font-weight: bold; fill: #000000; font-family: 'Times New Roman', serif; }
  </style>

  <!-- Figure Title -->
  <text x="500" y="45" class="figure-title">Figure 6.2: Lesson Management Workflow</text>

  <!-- 1. Start Terminator -->
  <rect x="390" y="80" width="220" height="48" rx="24" ry="24" fill="#f0f0f0" stroke="#000000" stroke-width="2" />
  <text x="500" y="110" class="terminator-text">Start</text>

  <line x1="500" y1="128" x2="500" y2="170" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 2. Teacher Login -->
  <rect x="230" y="170" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="203" class="box-title">Teacher Login</text>

  <line x1="500" y1="225" x2="500" y2="265" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 3. Access Lesson Management Module -->
  <rect x="230" y="265" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="298" class="box-title">Access Lesson Management Module</text>

  <line x1="500" y1="320" x2="500" y2="360" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 4. Create New Lesson -->
  <rect x="230" y="360" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="393" class="box-title">Create New Lesson</text>

  <line x1="500" y1="415" x2="500" y2="455" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 5. Enter Lesson Details -->
  <rect x="230" y="455" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="482" class="box-title">Enter Lesson Details</text>
  <text x="500" y="504" class="box-subtitle">(Title, Description, Subject, Grade Level)</text>

  <line x1="500" y1="520" x2="500" y2="560" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 6. Add Learning Content -->
  <rect x="230" y="560" width="540" height="65" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="587" class="box-title">Add Learning Content</text>
  <text x="500" y="609" class="box-subtitle">(Text, Images, Videos, Documents)</text>

  <line x1="500" y1="625" x2="500" y2="665" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 7. Save Lesson as Draft -->
  <rect x="230" y="665" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="698" class="box-title">Save Lesson as Draft</text>

  <line x1="500" y1="720" x2="500" y2="760" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 8. Review Lesson Content -->
  <rect x="230" y="760" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="793" class="box-title">Review Lesson Content</text>

  <line x1="500" y1="815" x2="500" y2="855" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 9. Decision Diamond: Lesson Complete? -->
  <polygon points="500,855 620,905 500,955 380,905" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="910" class="diamond-text">Lesson Complete?</text>

  <!-- Decision "Yes" Branch (Downwards) -->
  <line x1="500" y1="955" x2="500" y2="1005" class="connector-line" marker-end="url(#arrow-down)" />
  <text x="512" y="983" class="branch-label">Yes</text>

  <!-- Decision "No" Branch (Loop back to Node 6: Add Learning Content) -->
  <path d="M 380 905 L 140 905 L 140 592.5 L 230 592.5" fill="none" stroke="#000000" stroke-width="2" marker-end="url(#arrow-right)" />
  <text x="345" y="895" class="branch-label">No</text>
  <rect x="75" y="730" width="130" height="30" fill="#ffffff" stroke="#000000" stroke-width="1" />
  <text x="140" y="750" class="box-subtitle" style="font-weight: bold;">Edit Lesson Content</text>

  <!-- 10. Publish Lesson -->
  <rect x="230" y="1005" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1038" class="box-title">Publish Lesson</text>

  <line x1="500" y1="1060" x2="500" y2="1100" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 11. Store Lesson Information in Database -->
  <rect x="230" y="1100" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1133" class="box-title">Store Lesson Information in Database</text>

  <line x1="500" y1="1155" x2="500" y2="1195" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 12. Lesson Available to Students -->
  <rect x="230" y="1195" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1228" class="box-title">Lesson Available to Students</text>

  <line x1="500" y1="1250" x2="500" y2="1290" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 13. Students Access Lesson -->
  <rect x="230" y="1290" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1323" class="box-title">Students Access Lesson</text>

  <line x1="500" y1="1345" x2="500" y2="1385" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 14. Track Lesson Completion Status -->
  <rect x="230" y="1385" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1418" class="box-title">Track Lesson Completion Status</text>

  <line x1="500" y1="1440" x2="500" y2="1480" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 15. Generate Lesson Analytics -->
  <rect x="230" y="1480" width="540" height="55" fill="#ffffff" stroke="#000000" stroke-width="2" />
  <text x="500" y="1513" class="box-title">Generate Lesson Analytics</text>

  <line x1="500" y1="1535" x2="500" y2="1575" class="connector-line" marker-end="url(#arrow-down)" />

  <!-- 16. End Terminator -->
  <rect x="390" y="1575" width="220" height="48" rx="24" ry="24" fill="#f0f0f0" stroke="#000000" stroke-width="2" />
  <text x="500" y="1605" class="terminator-text">End</text>
</svg>"""

with open(SVG_OUTPUT_PATH, "w", encoding="utf-8") as f:
    f.write(svg_content)
print(f"SVG generated successfully: {SVG_OUTPUT_PATH}")

async def render_svg_to_png():
    async with async_playwright() as p:
        browser = await p.chromium.launch()
        page = await browser.new_page(device_scale_factor=2)
        await page.set_viewport_size({"width": 1000, "height": 1680})
        html_wrapper = f"""<!DOCTYPE html>
<html>
<head>
<style>
  body {{ margin: 0; padding: 0; background: white; width: 1000px; height: 1680px; overflow: hidden; }}
  svg {{ width: 1000px; height: 1680px; display: block; }}
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
