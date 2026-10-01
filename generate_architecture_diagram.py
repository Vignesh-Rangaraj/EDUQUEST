import os
from playwright.sync_api import sync_playwright

def create_svg():
    svg_content = '''<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 1700 1200" width="1700" height="1200" style="background-color: #ffffff; font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;">
  <defs>
    <!-- Arrowhead markers -->
    <marker id="arrow" viewBox="0 0 10 10" refX="6" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 0 1 L 10 5 L 0 9 z" fill="#111111" />
    </marker>
    <marker id="arrow-rev" viewBox="0 0 10 10" refX="4" refY="5" markerWidth="6" markerHeight="6" orient="auto-start-reverse">
      <path d="M 10 1 L 0 5 L 10 9 z" fill="#111111" />
    </marker>

    <!-- Drop Shadow Filter for visual depth -->
    <filter id="subtle-shadow" x="-5%" y="-5%" width="110%" height="110%">
      <feDropShadow dx="0" dy="2" stdDeviation="3" flood-opacity="0.06"/>
    </filter>
  </defs>

  <!-- Main Title Block -->
  <text x="850" y="38" text-anchor="middle" font-size="20" font-weight="bold" fill="#000000" letter-spacing="0.5">
    Figure 1. Overall System Architecture and Data Flow of the EduQuest Platform
  </text>
  <text x="850" y="60" text-anchor="middle" font-size="12" font-style="italic" fill="#555555">
    An IEEE/Scopus Compliant Architectural Blueprint for Offline-First Gamified LMS with AI Integration
  </text>

  <!-- ======================================================== -->
  <!-- 1. PRESENTATION TIER (FRONTEND LAYER)                    -->
  <!-- ======================================================== -->
  <rect x="60" y="85" width="1580" height="175" rx="8" ry="8" fill="#F8F9FA" stroke="#222222" stroke-width="2" filter="url(#subtle-shadow)"/>
  
  <rect x="80" y="97" width="220" height="24" rx="4" ry="4" fill="#111111"/>
  <text x="190" y="113" text-anchor="middle" font-size="12" font-weight="bold" fill="#FFFFFF">PRESENTATION TIER</text>
  
  <text x="1620" y="113" text-anchor="end" font-size="11" font-weight="bold" fill="#444444">
    Tech Stack: React.js | TypeScript | Vite | Tailwind CSS | ShadCN UI
  </text>

  <!-- Portals -->
  <!-- Student Portal -->
  <rect x="90" y="135" width="350" height="105" rx="6" ry="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
  <text x="265" y="160" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">Student Portal</text>
  <line x1="110" y1="170" x2="420" y2="170" stroke="#E0E0E0" stroke-width="1"/>
  <text x="265" y="190" text-anchor="middle" font-size="11" fill="#444444">• Interactive Gamified Learning Dashboard</text>
  <text x="265" y="210" text-anchor="middle" font-size="11" fill="#444444">• Offline Activity Engine &amp; AI Tutor Interface</text>

  <!-- Teacher Portal -->
  <rect x="480" y="135" width="350" height="105" rx="6" ry="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
  <text x="655" y="160" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">Teacher Portal</text>
  <line x1="500" y1="170" x2="810" y2="170" stroke="#E0E0E0" stroke-width="1"/>
  <text x="655" y="190" text-anchor="middle" font-size="11" fill="#444444">• Curriculum Authoring &amp; Activity Publishing</text>
  <text x="655" y="210" text-anchor="middle" font-size="11" fill="#444444">• Classroom Challenge &amp; Roster Management</text>

  <!-- School Administration Portal -->
  <rect x="870" y="135" width="350" height="105" rx="6" ry="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
  <text x="1045" y="160" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">School Admin Portal</text>
  <line x1="890" y1="170" x2="1200" y2="170" stroke="#E0E0E0" stroke-width="1"/>
  <text x="1045" y="190" text-anchor="middle" font-size="11" fill="#444444">• Institution Roster &amp; Classroom Analytics</text>
  <text x="1045" y="210" text-anchor="middle" font-size="11" fill="#444444">• School-wide Performance Telemetry</text>

  <!-- Super Admin Console -->
  <rect x="1260" y="135" width="360" height="105" rx="6" ry="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
  <text x="1440" y="160" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">Super Admin Console</text>
  <line x1="1280" y1="170" x2="1600" y2="170" stroke="#E0E0E0" stroke-width="1"/>
  <text x="1440" y="190" text-anchor="middle" font-size="11" fill="#444444">• System-wide Account Provisioning &amp; Roles</text>
  <text x="1440" y="210" text-anchor="middle" font-size="11" fill="#444444">• Platform Telemetry &amp; Database Governance</text>

  <!-- ======================================================== -->
  <!-- 2. CLIENT-SIDE OFFLINE TIER & SYNC ENGINE                 -->
  <!-- ======================================================== -->
  <!-- Offline Storage Tier (IndexedDB) -->
  <rect x="60" y="310" width="310" height="380" rx="8" ry="8" fill="#F8F9FA" stroke="#222222" stroke-width="2" stroke-dasharray="6,4" filter="url(#subtle-shadow)"/>
  <rect x="80" y="322" width="270" height="24" rx="4" ry="4" fill="#333333"/>
  <text x="215" y="338" text-anchor="middle" font-size="11" font-weight="bold" fill="#FFFFFF">OFFLINE STORAGE TIER (IndexedDB)</text>

  <g transform="translate(80, 360)">
    <rect x="0" y="0" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="62" y="25" text-anchor="middle" font-size="11" font-weight="bold">Lesson Cache</text>
    
    <rect x="145" y="0" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="207" y="25" text-anchor="middle" font-size="11" font-weight="bold">Quiz Cache</text>
    
    <rect x="0" y="55" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="62" y="80" text-anchor="middle" font-size="11" font-weight="bold">Game Cache</text>
    
    <rect x="145" y="55" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="207" y="80" text-anchor="middle" font-size="11" font-weight="bold">Activity Queue</text>
    
    <rect x="0" y="110" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="62" y="135" text-anchor="middle" font-size="11" font-weight="bold">XP Transactions</text>
    
    <rect x="145" y="110" width="125" height="42" rx="4" fill="#FFFFFF" stroke="#555555" stroke-width="1.2"/>
    <text x="207" y="135" text-anchor="middle" font-size="11" font-weight="bold">Coin Transactions</text>

    <rect x="0" y="165" width="270" height="50" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1.5"/>
    <text x="135" y="188" text-anchor="middle" font-size="12" font-weight="bold">Sync Queue &amp; State Repository</text>
    <text x="135" y="204" text-anchor="middle" font-size="10" fill="#555555">Local Persistence &amp; Unsynced Payload Buffer</text>
  </g>

  <!-- Offline Synchronization Engine -->
  <rect x="415" y="360" width="210" height="280" rx="8" ry="8" fill="#F4F5F7" stroke="#111111" stroke-width="2" filter="url(#subtle-shadow)"/>
  <rect x="430" y="372" width="180" height="38" rx="4" fill="#222222"/>
  <text x="520" y="388" text-anchor="middle" font-size="11" font-weight="bold" fill="#FFFFFF">OFFLINE SYNCHRONIZATION</text>
  <text x="520" y="402" text-anchor="middle" font-size="11" font-weight="bold" fill="#FFFFFF">ENGINE</text>

  <g transform="translate(430, 425)">
    <rect x="0" y="0" width="180" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="90" y="25" text-anchor="middle" font-size="11" font-weight="bold">Queue Processing</text>

    <rect x="0" y="52" width="180" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="90" y="77" text-anchor="middle" font-size="11" font-weight="bold">Conflict Resolution</text>

    <rect x="0" y="104" width="180" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="90" y="129" text-anchor="middle" font-size="11" font-weight="bold">Idempotent Sync Engine</text>

    <rect x="0" y="156" width="180" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="90" y="181" text-anchor="middle" font-size="11" font-weight="bold">Exponential Backoff Retry</text>
  </g>

  <!-- ======================================================== -->
  <!-- 3. APPLICATION TIER (BACKEND LAYER)                       -->
  <!-- ======================================================== -->
  <rect x="670" y="310" width="370" height="380" rx="8" ry="8" fill="#F8F9FA" stroke="#111111" stroke-width="2" filter="url(#subtle-shadow)"/>
  <rect x="690" y="322" width="330" height="24" rx="4" ry="4" fill="#111111"/>
  <text x="855" y="338" text-anchor="middle" font-size="12" font-weight="bold" fill="#FFFFFF">APPLICATION TIER (BACKEND LAYER)</text>

  <!-- Framework Tag -->
  <rect x="690" y="355" width="330" height="35" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1.2"/>
  <text x="855" y="377" text-anchor="middle" font-size="12" font-weight="bold" fill="#111111">Spring Boot 3 | Java 17 | REST Web Services</text>

  <!-- Security & RBAC Box -->
  <rect x="690" y="400" width="330" height="110" rx="6" ry="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
  <text x="855" y="422" text-anchor="middle" font-size="12" font-weight="bold" fill="#111111">Security &amp; Role-Based Access Control (RBAC)</text>
  <text x="855" y="440" text-anchor="middle" font-size="11" fill="#555555">Stateless JWT Authentication &amp; Spring Security Filter</text>
  <line x1="710" y1="448" x2="1000" y2="448" stroke="#E0E0E0" stroke-width="1"/>
  <g transform="translate(705, 458)">
    <rect x="0" y="0" width="70" height="35" rx="3" fill="#F0F0F0" stroke="#666666"/>
    <text x="35" y="21" text-anchor="middle" font-size="10" font-weight="bold">Student</text>

    <rect x="76" y="0" width="70" height="35" rx="3" fill="#F0F0F0" stroke="#666666"/>
    <text x="111" y="21" text-anchor="middle" font-size="10" font-weight="bold">Teacher</text>

    <rect x="152" y="0" width="74" height="35" rx="3" fill="#F0F0F0" stroke="#666666"/>
    <text x="189" y="21" text-anchor="middle" font-size="10" font-weight="bold">School Admin</text>

    <rect x="230" y="0" width="70" height="35" rx="3" fill="#F0F0F0" stroke="#666666"/>
    <text x="265" y="21" text-anchor="middle" font-size="10" font-weight="bold">Super Admin</text>
  </g>

  <!-- Core Services -->
  <g transform="translate(690, 520)">
    <rect x="0" y="0" width="160" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="80" y="25" text-anchor="middle" font-size="11" font-weight="bold">Gamification Engine</text>

    <rect x="170" y="0" width="160" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="250" y="25" text-anchor="middle" font-size="11" font-weight="bold">Curriculum Manager</text>

    <rect x="0" y="52" width="160" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="80" y="77" text-anchor="middle" font-size="11" font-weight="bold">Sync Controller</text>

    <rect x="170" y="52" width="160" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="250" y="77" text-anchor="middle" font-size="11" font-weight="bold">User &amp; Roster Service</text>
  </g>

  <!-- ======================================================== -->
  <!-- 4. AI INTEGRATION LAYER & GEMINI API                      -->
  <!-- ======================================================== -->
  <rect x="1080" y="340" width="240" height="320" rx="8" ry="8" fill="#F8F9FA" stroke="#111111" stroke-width="2" filter="url(#subtle-shadow)"/>
  <rect x="1095" y="352" width="210" height="24" rx="4" ry="4" fill="#222222"/>
  <text x="1200" y="368" text-anchor="middle" font-size="11" font-weight="bold" fill="#FFFFFF">AI INTEGRATION LAYER</text>

  <g transform="translate(1095, 390)">
    <rect x="0" y="0" width="210" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="105" y="25" text-anchor="middle" font-size="11" font-weight="bold">AI Learning Assistant</text>

    <rect x="0" y="52" width="210" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="105" y="77" text-anchor="middle" font-size="11" font-weight="bold">Gemini API Integration</text>

    <rect x="0" y="104" width="210" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="105" y="129" text-anchor="middle" font-size="11" font-weight="bold">Quiz &amp; Item Generation</text>

    <rect x="0" y="156" width="210" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="105" y="181" text-anchor="middle" font-size="11" font-weight="bold">Real-time Hint Generation</text>

    <rect x="0" y="208" width="210" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="105" y="233" text-anchor="middle" font-size="11" font-weight="bold">Lesson Summarization</text>
  </g>

  <!-- Cloud Node: Google Gemini API -->
  <g transform="translate(1360, 410)" filter="url(#subtle-shadow)">
    <!-- Cloud shape path -->
    <path d="M 40 80 
             A 30 30 0 0 1 50 25 
             A 40 40 0 0 1 120 15 
             A 35 35 0 0 1 180 35 
             A 30 30 0 0 1 200 80 
             A 25 25 0 0 1 180 120 
             L 40 120 
             A 25 25 0 0 1 40 80 Z" 
          fill="#FFFFFF" stroke="#111111" stroke-width="2"/>
    <text x="115" y="62" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">Google Gemini API</text>
    <text x="115" y="80" text-anchor="middle" font-size="11" fill="#555555">(External LLM Cloud)</text>
    <text x="115" y="96" text-anchor="middle" font-size="10" fill="#777777">Gemini 1.5 Pro / Flash Engine</text>
  </g>

  <!-- ======================================================== -->
  <!-- 5. LEARNING ANALYTICS SYSTEM                             -->
  <!-- ======================================================== -->
  <rect x="520" y="730" width="670" height="170" rx="8" ry="8" fill="#F8F9FA" stroke="#111111" stroke-width="2" filter="url(#subtle-shadow)"/>
  <rect x="540" y="742" width="630" height="24" rx="4" ry="4" fill="#111111"/>
  <text x="855" y="758" text-anchor="middle" font-size="12" font-weight="bold" fill="#FFFFFF">LEARNING ANALYTICS SYSTEM</text>

  <!-- Analytics Sub-Components -->
  <g transform="translate(540, 775)">
    <rect x="0" y="0" width="145" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="72" y="25" text-anchor="middle" font-size="11" font-weight="bold">Student Analytics</text>

    <rect x="160" y="0" width="145" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="232" y="25" text-anchor="middle" font-size="11" font-weight="bold">Teacher Analytics</text>

    <rect x="320" y="0" width="145" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="392" y="25" text-anchor="middle" font-size="11" font-weight="bold">School Analytics</text>

    <rect x="480" y="0" width="150" height="42" rx="4" fill="#FFFFFF" stroke="#444444" stroke-width="1.2"/>
    <text x="555" y="25" text-anchor="middle" font-size="11" font-weight="bold">System Analytics</text>
  </g>

  <!-- Dashboard Feeds Header -->
  <text x="855" y="842" text-anchor="middle" font-size="10" font-weight="bold" fill="#666666">REAL-TIME DASHBOARD OUTPUT FEEDS</text>

  <g transform="translate(540, 850)">
    <rect x="0" y="0" width="145" height="36" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1"/>
    <text x="72" y="22" text-anchor="middle" font-size="10" font-weight="bold">Student Dashboard</text>

    <rect x="160" y="0" width="145" height="36" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1"/>
    <text x="232" y="22" text-anchor="middle" font-size="10" font-weight="bold">Teacher Dashboard</text>

    <rect x="320" y="0" width="145" height="36" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1"/>
    <text x="392" y="22" text-anchor="middle" font-size="10" font-weight="bold">School Admin Dash</text>

    <rect x="480" y="0" width="150" height="36" rx="4" fill="#EAEAEA" stroke="#333333" stroke-width="1"/>
    <text x="555" y="22" text-anchor="middle" font-size="10" font-weight="bold">Super Admin Dash</text>
  </g>

  <!-- ======================================================== -->
  <!-- 6. DATA TIER (POSTGRESQL DATABASE)                        -->
  <!-- ======================================================== -->
  <rect x="520" y="940" width="670" height="190" rx="8" ry="8" fill="#F8F9FA" stroke="#111111" stroke-width="2" filter="url(#subtle-shadow)"/>
  <rect x="540" y="952" width="630" height="24" rx="4" ry="4" fill="#111111"/>
  <text x="855" y="968" text-anchor="middle" font-size="12" font-weight="bold" fill="#FFFFFF">DATA TIER (PERSISTENT STORAGE LAYER)</text>

  <!-- Database Cylinder Graphic & Label -->
  <g transform="translate(540, 988)">
    <rect x="0" y="0" width="630" height="128" rx="6" fill="#FFFFFF" stroke="#333333" stroke-width="1.5"/>
    
    <text x="315" y="24" text-anchor="middle" font-size="14" font-weight="bold" fill="#111111">PostgreSQL Relational Database</text>
    <line x1="20" y1="34" x2="610" y2="34" stroke="#E0E0E0" stroke-width="1"/>

    <g transform="translate(20, 48)">
      <rect x="0" y="0" width="110" height="60" rx="4" fill="#F4F5F7" stroke="#666666"/>
      <text x="55" y="28" text-anchor="middle" font-size="11" font-weight="bold">User Accounts</text>
      <text x="55" y="45" text-anchor="middle" font-size="9" fill="#666666">Auth &amp; RBAC</text>

      <rect x="120" y="0" width="110" height="60" rx="4" fill="#F4F5F7" stroke="#666666"/>
      <text x="175" y="28" text-anchor="middle" font-size="11" font-weight="bold">Curriculum</text>
      <text x="175" y="45" text-anchor="middle" font-size="9" fill="#666666">Modules &amp; Lessons</text>

      <rect x="240" y="0" width="110" height="60" rx="4" fill="#F4F5F7" stroke="#666666"/>
      <text x="295" y="28" text-anchor="middle" font-size="11" font-weight="bold">Schools &amp; Classes</text>
      <text x="295" y="45" text-anchor="middle" font-size="9" fill="#666666">Rosters &amp; Enrolment</text>

      <rect x="360" y="0" width="110" height="60" rx="4" fill="#F4F5F7" stroke="#666666"/>
      <text x="415" y="28" text-anchor="middle" font-size="11" font-weight="bold">Student Progress</text>
      <text x="415" y="45" text-anchor="middle" font-size="9" fill="#666666">Scores &amp; Badges</text>

      <rect x="480" y="0" width="110" height="60" rx="4" fill="#F4F5F7" stroke="#666666"/>
      <text x="535" y="28" text-anchor="middle" font-size="11" font-weight="bold">Analytics Data</text>
      <text x="535" y="45" text-anchor="middle" font-size="9" fill="#666666">Telemetry &amp; Logs</text>
    </g>
  </g>


  <!-- ======================================================== -->
  <!-- DATA FLOW ARROWS & CONNECTORS WITH NUMBERED LABELS        -->
  <!-- ======================================================== -->

  <!-- Flow 1: Teacher Portal -> Application Tier -> PostgreSQL -->
  <!-- Teacher Portal down to App Tier -->
  <path d="M 655 240 L 655 280 L 730 280 L 730 310" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="620" y="260" width="120" height="20" rx="10" fill="#111111"/>
  <text x="680" y="274" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">① Content Publishing</text>

  <!-- Flow 2: PostgreSQL -> IndexedDB (Prefetched Lesson Packages) -->
  <path d="M 520 1010 L 30 1010 L 30 500 L 60 500" fill="none" stroke="#111111" stroke-width="2" stroke-dasharray="5,4" marker-end="url(#arrow)"/>
  <rect x="180" y="998" width="180" height="20" rx="10" fill="#333333"/>
  <text x="270" y="1012" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">② Prefetched Lesson Packages</text>

  <!-- Flow 3: Student Activities -> IndexedDB -->
  <path d="M 265 240 L 265 310" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="200" y="260" width="130" height="20" rx="10" fill="#111111"/>
  <text x="265" y="274" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">③ Student Activities Log</text>

  <!-- Flow 4: IndexedDB -> Offline Synchronization Engine -->
  <path d="M 370 500 L 415 500" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="375" y="472" width="36" height="20" rx="10" fill="#111111"/>
  <text x="393" y="486" text-anchor="middle" font-size="11" font-weight="bold" fill="#FFFFFF">④</text>

  <!-- Flow 5 & 6: Sync Engine <-> Spring Boot Backend -->
  <path d="M 625 470 L 670 470" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 670 530 L 625 530" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="633" y="442" width="30" height="18" rx="9" fill="#111111"/>
  <text x="648" y="455" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">⑤</text>
  <rect x="633" y="540" width="30" height="18" rx="9" fill="#111111"/>
  <text x="648" y="553" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">⑥</text>

  <text x="648" y="496" text-anchor="middle" font-size="9" font-weight="bold" fill="#333333">REST Sync</text>
  <text x="648" y="515" text-anchor="middle" font-size="9" font-weight="bold" fill="#333333">Ack / Status</text>

  <!-- Flow 7: Backend -> PostgreSQL -->
  <path d="M 855 690 L 855 730" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <path d="M 855 900 L 855 940" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="795" y="700" width="120" height="20" rx="10" fill="#111111"/>
  <text x="855" y="714" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">⑦ DB Persistence</text>

  <!-- Flow 8: Learning Analytics System -> Dashboards -->
  <path d="M 1190 780 L 1230 780 L 1230 260 M 1230 260 L 1045 260 L 1045 240 M 1230 260 L 1440 260 L 1440 240" fill="none" stroke="#111111" stroke-width="1.8" stroke-dasharray="4,4" marker-end="url(#arrow)"/>
  <rect x="1170" y="750" width="120" height="20" rx="10" fill="#333333"/>
  <text x="1230" y="764" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">⑧ Analytics Feeds</text>

  <!-- Flow 9: Student Portal -> AI Integration Layer -> Gemini API -->
  <path d="M 370 135 L 370 105 L 1200 105 L 1200 340" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)"/>
  <rect x="710" y="93" width="160" height="22" rx="11" fill="#111111"/>
  <text x="790" y="108" text-anchor="middle" font-size="10" font-weight="bold" fill="#FFFFFF">⑨ AI Tutoring &amp; Hints Request</text>

  <path d="M 1320 500 L 1380 500" fill="none" stroke="#111111" stroke-width="2" marker-end="url(#arrow)" marker-start="url(#arrow-rev)"/>
  <rect x="1328" y="475" width="45" height="18" rx="9" fill="#111111"/>
  <text x="1350" y="488" text-anchor="middle" font-size="9" font-weight="bold" fill="#FFFFFF">HTTPS</text>

</svg>'''
    return svg_content

def main():
    scratch_dir = r"C:\Users\madhu\.gemini\antigravity\brain\48a568ea-2748-4efa-8dba-c10a93e17c92"
    svg_path = os.path.join(scratch_dir, "eduquest_system_architecture.svg")
    png_path = os.path.join(scratch_dir, "eduquest_system_architecture.png")

    svg_data = create_svg()
    with open(svg_path, "w", encoding="utf-8") as f:
        f.write(svg_data)
    print(f"SVG written to: {svg_path}")

    # Render PNG using Playwright
    with sync_playwright() as p:
        browser = p.chromium.launch()
        page = browser.new_page(viewport={"width": 1800, "height": 1300}, device_scale_factor=2)
        page.goto(f"file:///{svg_path.replace('\\', '/')}")
        page.locator("svg").screenshot(path=png_path)
        browser.close()
    print(f"High-Resolution PNG generated at: {png_path}")

if __name__ == "__main__":
    main()
