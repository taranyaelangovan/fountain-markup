import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CharacterFormat {
    public static void main (String args[]) throws Exception {
        Scanner sc=new Scanner(System.in);
        System.out.println("Enter file name: ");
        String filename=sc.nextLine();

        String content = "";
            try
            {
                content = new String (Files.readAllBytes(Paths.get(filename)));
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            
        //System.out.println("File content:\n"+content);
        //System.out.println(content.length());
        //System.out.println(content.indexOf("    "));
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        String charsetstring = new String(bytes, StandardCharsets.UTF_8);
        String outputfilename=filename.substring(0,filename.indexOf("."))+"out.txt";

        try {    
                FileWriter fw=new FileWriter(outputfilename);    
                fw.write(charsetstring);    
                fw.close();    
        }
        catch(Exception e) {
                System.out.println(e);
        } 
        

        //String[] lines = charsetstring.split("\\n");
        //lines.
        
        
       // int flag;
        int count=0,i=0;
        
        JSONArray JSONArr=new JSONArray();
        //JSONObject obj=new JSONObject();    
        //obj.put("type","text");
        int end, prev=-1,start,range;
        int lc=0;
        String str;
        
        char[] arr=new char[charsetstring.length()];
        
        /*int delcount=0;
        Integer[] delarray=new Integer[charsetstring.length()];
        int k=0;*/
        
        charsetstring = charsetstring.replaceAll("(?![\\r\\\t])\\p{C}", "");//no i18n
        
        for(i=0;i<charsetstring.length();i++)
        {
            arr[i]=charsetstring.charAt(i);
            String SceneHeadingRegex1="(INT. |EXT. )([a-zA-Z.,'()0-9 /-]*)";
            String SceneHeadingRegex2="([.])(.*)";
            String CharNameRegex="(\\s)*([A-Z' ]+)( )*(([(])([a-zA-Z.' ]*)([)]))*([\\^])*";
            String ParanthesisedRegex="(\\s)*([(])([a-zA-Z, ]*)([)])(\\s)*";
            String DialogueRegex="[a-zA-Z0-9.,?!': -]+";
            String CenteredRegex="([>])(.*)([<])";
            String LyricRegex="([~])(.*)";
            String TransitionRegex1="([A-Z ]*)( TO:)";
            String TransitionRegex2="([>])(.*)";
            String PageBreakRegex="[=]{3,}";
            String NoteLineRegex="([\\[]{2})(.*)([\\]]{2})";
            //String NoteRegex="(.*)(([\\\\[]{2})(.*)([\\\\]]{2}))(.*)";
            
            //type
            JSONObject inobj=new JSONObject();
            
            if((i>0)&&(i<charsetstring.length()-1)&&charsetstring.charAt(i)=='\r'){//&&charsetstring.charAt(i-1)!='\n') {
                inobj.put("type", "paragraph"); 
                //lc++;
                end=i;
                start=prev+1;
                //System.out.println(start+"\t"+end);
                str=charsetstring.substring(start, end);
                range=end+1-start; //length of current line
                
                
                //
                //if(lc<20)
                  //  System.out.println("start: "+start+"\tend: "+(end)+"\trange: "+range+"\t\t"+str);
                
                //style
                if((Pattern.matches(SceneHeadingRegex1, str))||(Pattern.matches(SceneHeadingRegex2, str)))
                    inobj.put("style","scene heading");
                else if(Pattern.matches(CharNameRegex, str))//&&lines[i-1].isBlank()&&!lines[i+1].isBlank())
                    inobj.put("style", "character name");
                else if(Pattern.matches(ParanthesisedRegex, str))
                    inobj.put("style","paranthesised");
                else if(Pattern.matches(CenteredRegex,str))
                    inobj.put("style","centered");
                else if(Pattern.matches(CenteredRegex,str))
                    inobj.put("style","centered");
                else if(Pattern.matches(LyricRegex,str))
                    inobj.put("style", "lyric");
                else if(Pattern.matches(PageBreakRegex,str))
                    inobj.put("style", "page break");
                else if(Pattern.matches(NoteLineRegex,str))
                    inobj.put("style", "note");
                else if((Pattern.matches(TransitionRegex1,str))||Pattern.matches(TransitionRegex2,str))
                    inobj.put("style","transition");
                else if(Pattern.matches(DialogueRegex,str)&&(i>=range)&&JSONArr.get(i-range).toString().contains("character name")&&charsetstring.charAt(i-range-1)=='^')//&&lines[i-1].charAt(lines[i-1].length()-1)=='^')
                    inobj.put("style","dual dialogue");
                else if(Pattern.matches(DialogueRegex,str)&&(((i>=range)&&JSONArr.get(i-range).toString().contains("character name"))||((i>=range)&&JSONArr.get(i-range).toString().contains("paranthesised"))))//||((i>=2)&&lines[i-1].isBlank()&&JSONArr.get(i-2).toString().contains("dialogue"))))
                    inobj.put("style", "dialogue");
                else
                    inobj.put("style","action");
                
                prev=i;
            }

            else
                inobj.put("type","text");
 
            JSONArr.add(inobj);
        }
        
        
        
        count=0;
        for(i=0;i<charsetstring.length();i++){
            if(i>2000)
                break;
            //if(charsetstring.charAt(i)!='\n')                    {
                System.out.println(i+"\t\t"+count+"|\t\t"+charsetstring.charAt(i)+"\t\t"+JSONArr.get(i));
                count++;
                if(charsetstring.charAt(i)=='\r'){
                    System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------");
                    count=0;
                }
        }
        
        JSONArray finallist = new JSONArray(); 
        finallist = (JSONArray)JSONArr.clone();

        
        //System.out.println(lines[0]+"\n"+finallist.get(0));
        for(i=0;i<JSONArr.size();i++)
        {
            //System.out.println(JSONArr.get(i)+" "+JSONArr.get(i-1));
            if((i>0)&&finallist.get(i).equals(JSONArr.get(i-1)))
            {
                if(charsetstring.charAt(i)!='\r')
                finallist.set(i,null);
            }
        }
        
        //JSONArr, finallist, charsetstring
  
        count=0;
        for(i=0;i<charsetstring.length();i++){
            if(i>2000)
                break;
            System.out.println(i+"\t\t"+count+"|\t\t"+charsetstring.charAt(i)+"\t\t"+finallist.get(i));
            count++;
            if(charsetstring.charAt(i)=='\r'){
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------");
                count=0;
            }

        }
    }
}

