package pickle;

import java.util.Arrays;

public final class PickleUtil {
    public static char[] checkComments(char[] textArray){
        // Loop through text array looking for double slash
        for(int i = 0; i < textArray.length - 1; i++){
            if(textArray[i] == '/' && textArray[i + 1] == '/'){
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
        }
        return textArray;
    }
}
