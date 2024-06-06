export interface PlayerLeaderboard {
  icon?: string
  username: string
  pp: number
  position: number
}

export function formatNumberWithSpaces(num: number): string {
  // Convert the number to a string
  let numStr = num.toString();

  // Use a regular expression to add spaces every three digits
  let formattedNumStr = numStr.replace(/\B(?=(\d{3})+(?!\d))/g, " ");

  return formattedNumStr;
}