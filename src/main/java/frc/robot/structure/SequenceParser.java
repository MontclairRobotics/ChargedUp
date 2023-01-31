package frc.robot.structure;

import java.util.ArrayList;

public class SequenceParser 
{
    //orange juice because i said so. "Cesca is so awesome" - Dylan & Abe (simultaneously)
    
    public static ArrayList<String> lex(String str)
    {
        // "1AB" -> [1, A, B]
        // "1 A B" -> [1, A, B]
        // "1 !A B" -> [1, !A, B]

        ArrayList<String> out = new ArrayList<String>();
        boolean isExclaimed = false;

        for(int i = 0; i < str.length(); i++)
        {
            char c = str.charAt(i);

            // Skip whitespace
            if(Character.isWhitespace(c))
            {
                continue;
            }
            // If we have a valid position, add it
            else if(c == '1' || c == '2' || c == '3' || c == 'A' || c == 'B' || c == 'C')
            {
                if(isExclaimed) out.add("!" + c);
                else            out.add(""  + c);

                isExclaimed = false;
            }
            // Check exclaimed
            else if(c == '!')
            {
                if(isExclaimed)
                {
                    return null;
                }

                isExclaimed = true;
            }
            // Error otherwise
            else 
            {
                return null;
            }
        }

        return out;
    }

    // 1AB
    // 1, 1A, A, AB, B
    public static ArrayList<String> parse(String str) 
    {
        // Lex
        ArrayList<String> in = lex(str);

        // Handle errors
        if(in == null) 
        {
            return null;
        }

        // Parse
        ArrayList<String> arr = new ArrayList<String>();

        for (int i = 0; i < in.size(); i++)
        {
            String commd = in.get(i);
            String trans = in.get(i) + in.get(i+1);

            if(!commd.contains("!")) 
            {
                arr.add(commd);
            }

            in.add(trans.replace("!", ""));
        }

        if(!in.get(in.size()-1).contains("!"))
        {
            arr.add(in.get(in.size() - 1));
        }

        return arr;
    }
}
