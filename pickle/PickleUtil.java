package pickle;

import java.util.Arrays;

public final class PickleUtil {

    /**
     * <p>
     * This function checks for comments on each line of code and then deletes
     *      them from the character array to be processed later in the scanner
     *
     * @param textArray  a character array that contains the character of a line in the code
     *
     * @return      char[]
     */
    public static char[] checkComments(char[] textArray){
        // Loop through text array looking for double slash
        boolean stringFlag = false;
        for(int i = 0; i < textArray.length - 1; i++){
            if(textArray[i] == '/' && textArray[i + 1] == '/' && !stringFlag){
                // if the // is at the beginning send an empty array back
                if((i - 1) > 0){
                    // Cut the comments out of the token array
                    textArray = Arrays.copyOfRange(textArray, 0, i);
                    //System.out.println("New Arr :" + String.valueOf(textArray));
                    break;
                }
                else{
                    textArray = new char[]{};
                    break;
                }
            }
            else if (textArray[i] == '\"' || textArray[i] == '\'') {
                // Swaps on when in string swaps off when meeting end quote
                stringFlag = !stringFlag;
            }
        }
        return textArray;
    }
}
