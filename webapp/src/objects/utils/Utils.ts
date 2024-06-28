export abstract class Utils {
  public static generateRandomColor(): string {
    // Generate random color components
    const r = Math.floor(Math.random() * 256).toString(16).padStart(2, '0');
    const g = Math.floor(Math.random() * 256).toString(16).padStart(2, '0');
    const b = Math.floor(Math.random() * 256).toString(16).padStart(2, '0');

    // Append '80' to the hex value for 50% opacity
    const opacity = '80';

    return `#${r}${g}${b}${opacity}`;
  }

  public static updateElapsedTime(startTime: number):string {
    const now = Date.now();
    const delta = now - startTime || 0;
    const hours = Math.floor(delta / 3600000);
    const minutes = Math.floor((delta % 3600000) / 60000);
    const seconds = Math.floor((delta % 60000) / 1000);
    return `${hours}:${minutes < 10 ? '0' : ''}${minutes}:${seconds < 10 ? '0' : ''}${seconds}`;
  };
}
