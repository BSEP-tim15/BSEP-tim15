export const validName = new RegExp ('^[a-zA-Z]+')

export const validCountry = new RegExp ('^[a-zA-Z]+')

export const validEmail = new RegExp ('^[a-zA-Z0-9._:$!%-]+@[a-zA-Z0-9.-]+.[a-zA-Z]$')

export const validPassword = new RegExp('^(?=.*[A-Z])(?=.*[a-z])(?=.*[0-9])(?=.*[!@#$%^&*()_+])')

export const containsDangerousCharacters = (inputData) => {
    if (inputData.includes("'") || inputData.includes("\"") || inputData.includes(";") || inputData.includes("--") || inputData.includes("=")
            || inputData.includes("xp_") || inputData.includes("/*") || inputData.includes("*/") || inputData.includes("\\")) {
        return true;
    } else {
        return false;
    }
}

