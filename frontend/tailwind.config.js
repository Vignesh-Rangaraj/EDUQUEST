/** @type {import('tailwindcss').Config} */
const neutral = {
  50: '#F8FAFC',
  100: '#F1F5F9',
  200: '#E2E8F0',
  300: '#CBD5E1',
  400: '#94A3B8',
  500: '#64748B',
  600: '#475569',
  700: '#334155',
  800: '#1E293B',
  850: '#172033',
  900: '#1E293B',
  950: '#0F172A'
};

const primary = {
  50: '#EFF6FF',
  100: '#DBEAFE',
  200: '#BFDBFE',
  300: '#93C5FD',
  400: '#60A5FA',
  500: '#3B82F6',
  600: '#2563EB',
  700: '#1D4ED8',
  800: '#1E40AF',
  850: '#1E3A8A',
  900: '#1E3A8A',
  950: '#172554'
};

const success = {
  50: '#F0FDF4',
  100: '#DCFCE7',
  200: '#BBF7D0',
  300: '#86EFAC',
  400: '#4ADE80',
  500: '#22C55E',
  600: '#16A34A',
  700: '#15803D',
  800: '#166534',
  850: '#14532D',
  900: '#14532D',
  950: '#052E16'
};

const accent = {
  50: '#FFFBEB',
  100: '#FEF3C7',
  200: '#FDE68A',
  300: '#FCD34D',
  400: '#FBBF24',
  500: '#F59E0B',
  600: '#D97706',
  700: '#B45309',
  800: '#92400E',
  850: '#78350F',
  900: '#78350F',
  950: '#451A03'
};

const danger = {
  50: '#FEF2F2',
  100: '#FEE2E2',
  200: '#FECACA',
  300: '#FCA5A5',
  400: '#F87171',
  500: '#EF4444',
  600: '#DC2626',
  700: '#B91C1C',
  800: '#991B1B',
  850: '#7F1D1D',
  900: '#7F1D1D',
  950: '#450A0A'
};

export default {
  content: [
    './index.html',
    './src/**/*.{js,ts,jsx,tsx}'
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        gray: neutral,
        slate: neutral,
        zinc: neutral,
        neutral,
        stone: neutral,
        blue: primary,
        sky: primary,
        indigo: primary,
        violet: primary,
        purple: primary,
        cyan: primary,
        green: success,
        emerald: success,
        teal: success,
        amber: accent,
        yellow: accent,
        orange: accent,
        red: danger,
        pink: danger,
        rose: danger,
        brand: primary,
        eduquest: {
          primary: '#2563EB',
          secondary: '#22C55E',
          accent: '#F59E0B',
          background: '#F8FAFC',
          surface: '#FFFFFF',
          text: '#1E293B',
          muted: '#64748B',
          border: '#E2E8F0',
          error: '#EF4444'
        }
      },
      borderRadius: {
        xl: '0.75rem',
        '2xl': '1rem',
        '3xl': '1rem'
      },
      boxShadow: {
        sm: '0 1px 3px rgba(15, 23, 42, 0.06)',
        DEFAULT: '0 2px 8px rgba(0, 0, 0, 0.08)',
        md: '0 2px 8px rgba(0, 0, 0, 0.08)',
        lg: '0 2px 8px rgba(0, 0, 0, 0.08)',
        xl: '0 2px 8px rgba(0, 0, 0, 0.08)',
        '2xl': '0 2px 8px rgba(0, 0, 0, 0.08)'
      }
    }
  },
  plugins: []
};
