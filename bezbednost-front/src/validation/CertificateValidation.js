export const validName = new RegExp ('^[a-zA-Z]+')

export const containsDangerousCharacters = (inputData) => {
    if (inputData.includes("'") || inputData.includes("\"") || inputData.includes(";") || inputData.includes("--") || inputData.includes("=")
            || inputData.includes("xp_") || inputData.includes("/*") || inputData.includes("*/") || inputData.includes("\\")) {
        return true;
    } else {
        return false;
    }
}