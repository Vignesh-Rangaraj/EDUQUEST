import os
import sys
from playwright.sync_api import sync_playwright

def generate_svg():
    svg_content = """<?xml version="1.0" encoding="UTF-8" standalone="no"?>
<svg width="1000" height="2060" viewBox="0 0 1000 2060" xmlns="http://www.w3.org/2000/svg">
  <defs>
    <style>
      .title { font-family: 'Times New Roman', serif; font-size: 24px; font-weight: bold; fill: #000000; text-anchor: middle; }
      .box { fill: #ffffff; stroke: #000000; stroke-width: 2; }
      .box-text { font-family: 'Times New Roman', serif; font-size: 16px; font-weight: bold; fill: #000000; text-anchor: middle; dominant-baseline: middle; }
      .sub-text { font-family: 'Times New Roman', serif; font-size: 13px; font-weight: normal; fill: #000000; text-anchor: middle; dominant-baseline: middle; }
      .diamond-text { font-family: 'Times New Roman', serif; font-size: 15px; font-weight: bold; fill: #000000; text-anchor: middle; dominant-baseline: middle; }
      .line { stroke: #000000; stroke-width: 2; fill: none; }
      .arrow { fill: #000000; }
      .label-text { font-family: 'Times New Roman', serif; font-size: 14px; font-weight: bold; fill: #000000; text-anchor: middle; }
      .action-box-text { font-family: 'Times New Roman', serif; font-size: 13px; font-weight: bold; fill: #000000; text-anchor: middle; dominant-baseline: middle; }
    </style>
    
    <!-- Marker for straight arrow heads -->
    <marker id="arrow" viewBox="0 0 10 10" refX="10" refY="5" markerWidth="7" markerHeight="7" orient="auto-start-reverse">
      <path d="M 0 0 L 10 5 L 0 10 z" class="arrow" />
    </marker>
  </defs>

  <!-- Background -->
  <rect width="1000" height="2060" fill="#ffffff" />

  <!-- Title -->
  <text x="500" y="45" class="title">Figure 6.6: Offline Learning and Synchronization Workflow</text>

  <!-- 1. Start -->
  <rect x="400" y="85" width="200" height="50" rx="25" ry="25" class="box" />
  <text x="500" y="110" class="box-text">Start</text>

  <!-- Arrow 1 -> 2 -->
  <path d="M 500 135 L 500 175" class="line" marker-end="url(#arrow)" />

  <!-- 2. Student Login -->
  <rect x="280" y="175" width="440" height="50" class="box" />
  <text x="500" y="200" class="box-text">Student Login</text>

  <!-- Arrow 2 -> 3 -->
  <path d="M 500 225 L 500 265" class="line" marker-end="url(#arrow)" />

  <!-- 3. Check Network Connectivity -->
  <rect x="280" y="265" width="440" height="50" class="box" />
  <text x="500" y="290" class="box-text">Check Network Connectivity</text>

  <!-- Arrow 3 -> 4 -->
  <path d="M 500 315 L 500 355" class="line" marker-end="url(#arrow)" />

  <!-- 4. Decision: Internet Available? -->
  <polygon points="500,355 640,405 500,455 360,405" class="box" />
  <text x="500" y="405" class="diamond-text">Internet Available?</text>

  <!-- Decision 1 Branches: Yes (Right -> Server) & No (Left -> IndexedDB) -->
  <!-- 'No' Branch to Left -->
  <path d="M 360 405 L 260 405 L 260 475" class="line" marker-end="url(#arrow)" />
  <text x="310" y="395" class="label-text">No</text>

  <!-- 5B. Load Data from IndexedDB Local Storage -->
  <rect x="70" y="475" width="380" height="50" class="box" />
  <text x="260" y="500" class="box-text">Load Data from IndexedDB Local Storage</text>

  <!-- 'Yes' Branch to Right -->
  <path d="M 640 405 L 740 405 L 740 475" class="line" marker-end="url(#arrow)" />
  <text x="690" y="395" class="label-text">Yes</text>

  <!-- 5A. Load Data from Server -->
  <rect x="570" y="475" width="340" height="50" class="box" />
  <text x="740" y="500" class="box-text">Load Data from Server</text>

  <!-- Merge 5A & 5B down to Node 6 -->
  <!-- Line from 5B (260, 525) -> down to 565 -> right to 500 -->
  <path d="M 260 525 L 260 565 L 500 565" class="line" />
  <!-- Line from 5A (740, 525) -> down to 565 -> left to 500 -->
  <path d="M 740 525 L 740 565 L 500 565" class="line" />
  <!-- Combined line down to Node 6 -->
  <path d="M 500 565 L 500 595" class="line" marker-end="url(#arrow)" />

  <!-- 6. Access Lessons and Activities -->
  <rect x="280" y="595" width="440" height="50" class="box" />
  <text x="500" y="620" class="box-text">Access Lessons and Activities</text>

  <!-- Arrow 6 -> 7 -->
  <path d="M 500 645 L 500 685" class="line" marker-end="url(#arrow)" />

  <!-- 7. Complete Learning Activities -->
  <rect x="280" y="685" width="440" height="60" class="box" />
  <text x="500" y="706" class="box-text">Complete Learning Activities</text>
  <text x="500" y="728" class="sub-text">(Lessons, Quizzes, Games, AI Assistance)</text>

  <!-- Arrow 7 -> 8 -->
  <path d="M 500 745 L 500 785" class="line" marker-end="url(#arrow)" />

  <!-- 8. Store Progress Locally -->
  <rect x="280" y="785" width="440" height="50" class="box" />
  <text x="500" y="810" class="box-text">Store Progress Locally</text>

  <!-- Arrow 8 -> 9 -->
  <path d="M 500 835 L 500 875" class="line" marker-end="url(#arrow)" />

  <!-- 9. Save Quiz Attempts and Scores -->
  <rect x="280" y="875" width="440" height="50" class="box" />
  <text x="500" y="900" class="box-text">Save Quiz Attempts and Scores</text>

  <!-- Arrow 9 -> 10 -->
  <path d="M 500 925 L 500 965" class="line" marker-end="url(#arrow)" />

  <!-- 10. Save XP, Rewards, and Achievements -->
  <rect x="280" y="965" width="440" height="50" class="box" />
  <text x="500" y="990" class="box-text">Save XP, Rewards, and Achievements</text>

  <!-- Arrow 10 -> 11 -->
  <path d="M 500 1015 L 500 1055" class="line" marker-end="url(#arrow)" />

  <!-- 11. Monitor Network Status -->
  <rect x="280" y="1055" width="440" height="50" class="box" />
  <text x="500" y="1080" class="box-text">Monitor Network Status</text>

  <!-- Arrow 11 -> 12 -->
  <path d="M 500 1105 L 500 1145" class="line" marker-end="url(#arrow)" />

  <!-- 12. Decision: Connection Restored? -->
  <polygon points="500,1145 640,1195 500,1245 360,1195" class="box" />
  <text x="500" y="1195" class="diamond-text">Connection Restored?</text>

  <!-- Decision 2 Branches: No (Left & Loop Up) & Yes (Down) -->
  <!-- 'No' Branch: Left to X=110, Up to Y=620 (Node 6 level), Right to Node 6 (X=280) -->
  <path d="M 360 1195 L 110 1195 L 110 620 L 280 620" class="line" marker-end="url(#arrow)" />
  <text x="325" y="1185" class="label-text">No</text>
  
  <!-- Action Box along the return loop -->
  <rect x="30" y="885" width="160" height="40" class="box" />
  <text x="110" y="897" class="action-box-text">Continue Offline</text>
  <text x="110" y="913" class="action-box-text">Learning</text>

  <!-- 'Yes' Branch: Down to Node 13 -->
  <path d="M 500 1245 L 500 1285" class="line" marker-end="url(#arrow)" />
  <text x="520" y="1265" class="label-text">Yes</text>

  <!-- 13. Initiate Synchronization Service -->
  <rect x="280" y="1285" width="440" height="50" class="box" />
  <text x="500" y="1310" class="box-text">Initiate Synchronization Service</text>

  <!-- Arrow 13 -> 14 -->
  <path d="M 500 1335 L 500 1375" class="line" marker-end="url(#arrow)" />

  <!-- 14. Upload Local Learning Records -->
  <rect x="280" y="1375" width="440" height="50" class="box" />
  <text x="500" y="1400" class="box-text">Upload Local Learning Records</text>

  <!-- Arrow 14 -> 15 -->
  <path d="M 500 1425 L 500 1465" class="line" marker-end="url(#arrow)" />

  <!-- 15. Update PostgreSQL Database -->
  <rect x="280" y="1465" width="440" height="50" class="box" />
  <text x="500" y="1490" class="box-text">Update PostgreSQL Database</text>

  <!-- Arrow 15 -> 16 -->
  <path d="M 500 1515 L 500 1555" class="line" marker-end="url(#arrow)" />

  <!-- 16. Resolve Data Conflicts -->
  <rect x="280" y="1555" width="440" height="50" class="box" />
  <text x="500" y="1580" class="box-text">Resolve Data Conflicts</text>

  <!-- Arrow 16 -> 17 -->
  <path d="M 500 1605 L 500 1645" class="line" marker-end="url(#arrow)" />

  <!-- 17. Update Student Progress -->
  <rect x="280" y="1645" width="440" height="50" class="box" />
  <text x="500" y="1670" class="box-text">Update Student Progress</text>

  <!-- Arrow 17 -> 18 -->
  <path d="M 500 1695 L 500 1735" class="line" marker-end="url(#arrow)" />

  <!-- 18. Refresh Analytics Dashboard -->
  <rect x="280" y="1735" width="440" height="50" class="box" />
  <text x="500" y="1760" class="box-text">Refresh Analytics Dashboard</text>

  <!-- Arrow 18 -> 19 -->
  <path d="M 500 1785 L 500 1825" class="line" marker-end="url(#arrow)" />

  <!-- 19. Synchronization Successful -->
  <rect x="280" y="1825" width="440" height="50" class="box" />
  <text x="500" y="1850" class="box-text">Synchronization Successful</text>

  <!-- Arrow 19 -> 20 -->
  <path d="M 500 1875 L 500 1915" class="line" marker-end="url(#arrow)" />

  <!-- 20. End -->
  <rect x="400" y="1915" width="200" height="50" rx="25" ry="25" class="box" />
  <text x="500" y="1940" class="box-text">End</text>

</svg>
"""
    return svg_content

def main():
    artifact_dir = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92"
    svg_path = os.path.join(artifact_dir, "figure_6_6_workflow.svg")
    png_path = os.path.join(artifact_dir, "figure_6_6_workflow.png")

    svg_content = generate_svg()

    with open(svg_path, "w", encoding="utf-8") as f:
        f.write(svg_content)
    print(f"SVG generated successfully: {svg_path}")

    # Render PNG with Playwright
    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page(viewport={"width": 1000, "height": 2060}, device_scale_factor=2)
        html_wrapper = f"""
        <!DOCTYPE html>
        <html>
        <head>
          <style>
            body {{ margin: 0; padding: 0; background: #ffffff; }}
          </style>
        </head>
        <body>
          {svg_content}
        </body>
        </html>
        """
        page.set_content(html_wrapper, wait_until="load")
        page.locator("svg").screenshot(path=png_path)
        browser.close()

    print(f"High-Res PNG rendered successfully: {png_path}")

if __name__ == "__main__":
    main()
