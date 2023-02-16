package frc.robot.structure;

import java.util.ArrayList;

import frc.robot.structure.helpers.Logging;

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
    public static String[] lex(String str)
    {
        if(str.length() == 0)
        {
            Logging.error("Empty auto command provided.");
            return null;
        }

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
            else if(c == '1' || c == '2' || c == '3' || c == 'A' || c == 'B' || c == 'C' || c == 'a' || c == 'b' || c == 'c')
            {
                c = Character.toUpperCase(c);

                if(isExclaimed) out.add("!" + c);
                else            out.add(""  + c);

                isExclaimed = false;
            }
            // Check exclaimed
            else if(c == '!')
            {
                if(isExclaimed)
                {
                    Logging.error("Dual '!' present in command string! '" + str + "'");
                    return null;
                }

                isExclaimed = true;
            }
            // Error otherwise
            else 
            {
                Logging.error("Unexpected character " + c + " in command string! '" + str + "'");
                return null;
            }
        }

        return out.toArray(String[]::new);
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
    public static String[] parse(String str) 
    {
        // Lex
        String[] lex = lex(str);

        // Handle errors
        if(lex == null) 
        {
            return null;
        }

        // Parse
        ArrayList<String> output = new ArrayList<String>();

        for (int i = 0; i < lex.length-1; i++)
        {
            String commd = lex[i];
            String trans = lex[i] + lex[i+1];

            if(!commd.contains("!")) 
            {
                output.add(commd);
            }

            output.add(trans.replace("!", ""));
        }

        if(!lex[lex.length - 1].contains("!"))
        {
            output.add(lex[lex.length - 1]);
        }

        return output.toArray(String[]::new);
    }
}