import React, { useMemo } from 'react';

const ELEMENTS: Record<string, keyof React.JSX.IntrinsicElements> = {
  H1: 'h2', H2: 'h3', H3: 'h4', H4: 'h5', H5: 'h6', H6: 'h6',
  P: 'p', DIV: 'div', SECTION: 'section', UL: 'ul', OL: 'ol', LI: 'li',
  STRONG: 'strong', B: 'strong', EM: 'em', I: 'em', BR: 'br',
  BLOCKQUOTE: 'blockquote', CODE: 'code', PRE: 'pre', HR: 'hr'
};

const OMITTED_ELEMENTS = new Set(['SCRIPT', 'STYLE', 'IFRAME', 'OBJECT', 'EMBED', 'SVG', 'MATH', 'TEMPLATE']);
const STYLES: Partial<Record<keyof React.JSX.IntrinsicElements, string>> = {
  h2: 'text-2xl font-bold text-gray-900 dark:text-white mt-6 mb-3 first:mt-0',
  h3: 'text-xl font-bold text-gray-900 dark:text-white mt-5 mb-2',
  h4: 'text-lg font-semibold text-gray-900 dark:text-white mt-4 mb-2',
  h5: 'text-base font-semibold mt-4 mb-2', h6: 'text-sm font-semibold mt-3 mb-2',
  p: 'mb-3 last:mb-0', ul: 'list-disc pl-6 mb-4 space-y-1', ol: 'list-decimal pl-6 mb-4 space-y-1',
  blockquote: 'border-l-4 border-sky-300 pl-4 italic my-4', code: 'rounded bg-gray-100 dark:bg-gray-700 px-1 py-0.5 font-mono text-[0.95em]',
  pre: 'overflow-x-auto rounded-lg bg-gray-100 dark:bg-gray-700 p-4 my-4 font-mono'
};

function renderNode(node: Node, key: string): React.ReactNode {
  if (node.nodeType === Node.TEXT_NODE) return node.textContent;
  if (node.nodeType !== Node.ELEMENT_NODE) return null;

  const element = node as Element;
  if (OMITTED_ELEMENTS.has(element.tagName)) return null;
  const tag = ELEMENTS[element.tagName];
  const children = Array.from(element.childNodes).map((child, index) => renderNode(child, `${key}-${index}`));
  if (!tag) return <React.Fragment key={key}>{children}</React.Fragment>;

  return React.createElement(tag, { key, className: STYLES[tag] }, ...children);
}

/** Displays stored lesson HTML using a small allowlist; attributes and active content are discarded. */
export const LessonContentView: React.FC<{ content: string }> = ({ content }) => {
  const rendered = useMemo(() => {
    if (typeof DOMParser === 'undefined' || !content.includes('<')) return content;
    const parsed = new DOMParser().parseFromString(content, 'text/html');
    return Array.from(parsed.body.childNodes).map((node, index) => renderNode(node, `lesson-${index}`));
  }, [content]);

  return <div className="lesson-rich-content">{rendered}</div>;
};
