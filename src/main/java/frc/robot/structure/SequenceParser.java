package frc.robot.structure;

import java.util.ArrayList;

/**
 * A collection of static methods relating to the parsing of autonomous sequence strings.
 */
public class SequenceParser 
{
    private SequenceParser() {}

    //orange juice because i said so. "Cesca is so awesome" - Dylan & Abe (simultaneously)
    
    /**
     * Lex an autonomous sequence string into its components.
     * Skips over any whitespace characters and appends modifiers 
     * to their bases ("!A" remains conjoined while " A" becomes "A").
     * 
     * @param str The autonomous sequence string
     * @return The components of the path (i.e. '1', 'A', or '!1'), or null if lexing fails
     */
    public static ArrayList<String> lex(String str)
    {
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
    
    /**
     * Parse an autonomous sequence string into the commands which it will execute.
     * 
     * First, this method lexes the inpur using {@link #lex(String)}, then
     * generates the list of commands which the sequence will compris, including
     * both actions like "A" or "B" and transitions like "AB" and "1C".
     * 
     * Skips any commands attributed with '!'.
     * 
     * @param str The autonomous sequence string
     * @return A list which contains identifiers for the commands which comprise the autonomous routine, 
     * or null if lexing or parsing fails
     */
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
