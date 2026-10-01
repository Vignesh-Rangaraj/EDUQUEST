const http = require('http');

function post(url, data) {
  return new Promise((resolve, reject) => {
    const u = new URL(url);
    const req = http.request({
      hostname: u.hostname,
      port: u.port,
      path: u.pathname,
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    }, res => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => resolve(JSON.parse(body)));
    });
    req.on('error', reject);
    req.write(JSON.stringify(data));
    req.end();
  });
}

function get(url, token) {
  return new Promise((resolve, reject) => {
    const u = new URL(url);
    const req = http.request({
      hostname: u.hostname,
      port: u.port,
      path: u.pathname,
      method: 'GET',
      headers: { 'Authorization': 'Bearer ' + token }
    }, res => {
      let body = '';
      res.on('data', chunk => body += chunk);
      res.on('end', () => resolve(JSON.parse(body)));
    });
    req.on('error', reject);
    req.end();
  });
}

async function verify() {
  const auth = await post('http://localhost:8081/api/auth/login', { username: 'student_6a_1', password: 'password123' });
  const activities = await get('http://localhost:8081/api/student/activities', auth.token);

  console.log('--- VERIFYING QUESTION PROMPT EXTRACTION FOR ALL 8 GAMES ---');
  for (const act of activities) {
    const rawCfg = await get('http://localhost:8081/api/student/activities/' + act.id + '/game-config', auth.token);
    const config = typeof rawCfg.jsonConfiguration === 'string' ? JSON.parse(rawCfg.jsonConfiguration) : rawCfg.jsonConfiguration;
    
    let questions = [];
    if (act.activityType === 'SHOOT_THE_ANSWER' || act.activityType === 'BALLOON_POP') {
      const raw = config?.questions || config?.items || [];
      questions = raw.map(q => ({
        prompt: q.questionText || q.prompt || q.question || q.clue || 'Question',
        correctAnswer: (q.correctAnswer || q.answer || '').toString(),
        options: q.options || []
      }));
    } else if (act.activityType === 'MATCH_THE_FOLLOWING') {
      const raw = config?.pairs || config?.questions || [];
      questions = raw.map(p => ({
        prompt: p.left || p.prompt || p.question,
        correctAnswer: p.right || p.correctAnswer
      }));
    } else if (act.activityType === 'WORD_SCRAMBLE') {
      const raw = config?.words || config?.questions || [];
      questions = raw.map(w => ({
        prompt: w.scrambled || w.word || w.prompt,
        correctAnswer: w.target || w.correctAnswer
      }));
    } else if (act.activityType === 'FLASH_CARDS') {
      const raw = config?.cards || config?.questions || [];
      questions = raw.map(c => ({
        prompt: c.front || c.prompt,
        correctAnswer: c.back || c.correctAnswer
      }));
    } else if (act.activityType === 'TREASURE_HUNT') {
      const raw = config?.stages || config?.questions || [];
      questions = raw.map(s => ({
        prompt: s.clue || s.question || s.prompt,
        correctAnswer: s.correctAnswer
      }));
    } else {
      const raw = config?.questions || [];
      questions = raw.map(q => ({
        prompt: q.questionText || q.prompt || q.question,
        correctAnswer: q.correctAnswer
      }));
    }

    console.log('\nGame: ' + act.title + ' (' + act.activityType + ')');
    questions.forEach((q, idx) => {
      console.log('  Step ' + (idx + 1) + ' Question Prompt: "' + q.prompt + '" | Answer: "' + q.correctAnswer + '"');
    });
  }
}

verify();
