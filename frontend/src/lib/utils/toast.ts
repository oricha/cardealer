// Simple toast utility - can be enhanced to use the existing toast system
export const toast = {
  success: (message: string) => {
    console.log('✅ Success:', message);
    // TODO: Integrate with the existing toast system when available
  },
  error: (message: string) => {
    console.error('❌ Error:', message);
    // TODO: Integrate with the existing toast system when available
  },
  info: (message: string) => {
    console.log('ℹ️ Info:', message);
    // TODO: Integrate with the existing toast system when available
  },
  warning: (message: string) => {
    console.warn('⚠️ Warning:', message);
    // TODO: Integrate with the existing toast system when available
  },
};