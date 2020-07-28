import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.*;
import java.nio.charset.StandardCharsets;
import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

public class CharFormat {
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
        

        int count=0,i=0;

        int end, prev=-1,start,range;
        int lc=0;
        String str;
 
        
        charsetstring = charsetstring.replaceAll("(?![\\r\\\t])\\p{C}", "");//no i18n
        
        JSONArray JSONArr=new JSONArray();
        for (i=0;i<charsetstring.length();i++)
        {
            JSONObject o=new JSONObject();
            
            JSONArr.add(o);
        }
        
        //emphasis checking
        int j;
        for(i=0;i<charsetstring.length();i++){
            int remove=0;
            int startindex=0;
            int endindex=0;
            if(charsetstring.charAt(i)=='*' && charsetstring.charAt(i+1)=='*' && charsetstring.charAt(i+2)=='*'){
                
                i=i+3; 
                startindex=i;
                while(!(charsetstring.charAt(i)=='*' && charsetstring.charAt(i+1)=='*' && charsetstring.charAt(i+2)=='*')) {
                        i++; 
                        count++;
                        //emphasis.add("italic");
                }
                //emphasis.add("end of italics");
                endindex=i;

                String contentstr=charsetstring.substring(startindex, endindex);

                String empstr=charsetstring.substring(startindex-3, endindex+3);

                ///System.out.println(contentstr+" "+empstr);
                remove=empstr.length()-contentstr.length();
                charsetstring=charsetstring.replace(empstr, contentstr);
                JSONArr.remove(startindex-1);
                JSONArr.remove(endindex+1);
                JSONArr.remove(startindex-2);
                JSONArr.remove(endindex+2);
                JSONArr.remove(startindex-3);
                JSONArr.remove(endindex+3);

                for(j=startindex-3;j<endindex-3;j++){
                   // emphasis.set(j,"bold italics");          
                   JSONObject emp=(JSONObject)JSONArr.get(j);
                   emp.put("style","bold italics");
                   JSONArr.set(j, emp);
                }

            }
            else if(charsetstring.charAt(i)=='*' && charsetstring.charAt(i+1)=='*'){
                    i=i+2; 
                    startindex=i;
                    while(!(charsetstring.charAt(i)=='*' && charsetstring.charAt(i+1)=='*')) {
                            i++; 
                            count++;
                            //emphasis.add("italic");
                    }
                    //emphasis.add("end of italics");
                    endindex=i;

                    String contentstr=charsetstring.substring(startindex, endindex);

                    String empstr=charsetstring.substring(startindex-2, endindex+2);

                    //System.out.println(contentstr+" "+empstr);
                    remove=empstr.length()-contentstr.length();
                    charsetstring=charsetstring.replace(empstr, contentstr);
                    JSONArr.remove(startindex-1);
                    JSONArr.remove(endindex+1);
                    JSONArr.remove(startindex-2);
                    JSONArr.remove(endindex+2);

                    for(j=startindex-2;j<endindex-2;j++){
                            //emphasis.set(j,"bold");
                        JSONObject emp=(JSONObject)JSONArr.get(j);
                        emp.put("style","bold");
                        JSONArr.set(j, emp);
                    }
            }
            else if(charsetstring.charAt(i)=='*')
            {
                    //emphasis.add("start of italics");
                    i++; 
                    startindex=i;
                    while(charsetstring.charAt(i)!='*'){
                            i++; 
                            count++;
                            //emphasis.add("italic");
                    }
                    //emphasis.add("end of italics");
                    endindex=i;
                    //char[] arr=new char[count+2];

                    String contentstr=charsetstring.substring(startindex, endindex);

                    String empstr=charsetstring.substring(startindex-1, endindex+1);

                    //System.out.println(contentstr+" "+empstr);
                    remove=empstr.length()-contentstr.length();
                    charsetstring=charsetstring.replace(empstr, contentstr);
                    JSONArr.remove(startindex-1);
                    JSONArr.remove(endindex+1);

                    for(j=startindex-1;j<endindex-1;j++){
                           // emphasis.set(j,"italics");
                        JSONObject emp=(JSONObject)JSONArr.get(j);
                        emp.put("style","italics");
                        JSONArr.set(j, emp);
                    }
            }
            else if(charsetstring.charAt(i)=='_')
            {
                    //emphasis.add("start of italics");
                    i++; 
                    startindex=i;
                    while(charsetstring.charAt(i)!='_'){
                            i++; 
                            count++;
                            //emphasis.add("italic");
                    }
                    //emphasis.add("end of italics");
                    endindex=i;
                    //char[] arr=new char[count+2];

                    String contentstr=charsetstring.substring(startindex, endindex);

                    String empstr=charsetstring.substring(startindex-1, endindex+1);

                    //System.out.println(contentstr+" "+empstr);
                    remove=empstr.length()-contentstr.length();
                    charsetstring=charsetstring.replace(empstr, contentstr);
                    //replace in JSONArr too
                    JSONArr.remove(startindex-1);
                    JSONArr.remove(endindex+1);
                    for(j=startindex-1;j<endindex-1;j++){
                            //emphasis.set(j,"underline");
                        JSONObject emp=(JSONObject)JSONArr.get(j);
                        emp.put("style","underline");
                        JSONArr.set(j, emp);
                    }
            }
            else{
                   // emphasis.add("none");
            }
            i=i-remove;
        }
        
        
        for(i=0;i<charsetstring.length();i++)
        {
            //arr[i]=charsetstring.charAt(i);
            String SceneHeadingRegex1="(INT. |EXT. )([a-zA-Z.,'()0-9 /-]*)";
            String SceneHeadingRegex2="([.])(.*)";
            String CharNameRegex="(\\s)*([A-Z' ]+)( )*(([(])([a-zA-Z.' ]*)([)]))*([\\^])*";
            String ParanthesisedRegex="(\\s)*([(])([a-zA-Z, ]*)([)])(\\s)*";
            String DialogueRegex="(\\s)*[a-zA-Z0-9.,?!': -_*]+(\\s)*";
            String CenteredRegex="([>])(.*)([<])";
            String LyricRegex="([~])(.*)";
            String TransitionRegex1="(\\s)*([A-Z ]*)( TO:)";
            String TransitionRegex2="([>])(.*)";
            String PageBreakRegex="[=]{3,}";
            String NoteLineRegex="([\\[]{2})(.*)([\\]]{2})";
            
            //type
            JSONObject inobj=new JSONObject();
            inobj=(JSONObject)JSONArr.get(i);
            
            if((i>0)&&(i<charsetstring.length()-1)&&charsetstring.charAt(i)=='\r'){//&&charsetstring.charAt(i-1)!='\n') {
                inobj.put("type", "paragraph"); 
                //lc++;
                end=i;
                start=prev+1;
                //System.out.println(start+"\t"+end);
                str=charsetstring.substring(start, end);
                range=end+1-start; //length of current line
                
                
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

            JSONArr.set(i, inobj);
        }
        

        
        System.out.println("length: "+charsetstring.length()+"  ");
        
        
        JSONArray finallist = new JSONArray(); 
        finallist = (JSONArray)JSONArr.clone();

        for(i=0;i<JSONArr.size();i++)
        {

            if((i>0)&&finallist.get(i).equals(JSONArr.get(i-1)))
            {
                if(charsetstring.charAt(i)!='\r')
                    finallist.set(i,null);
            }
        }
  
        count=0;
        for(i=0;i<charsetstring.length();i++){
            //if(i>2000)
              //  break;
            System.out.println(i+"\t\t"+count+"|\t\t"+charsetstring.charAt(i)+"\t\t"+finallist.get(i));
            count++;
            if(charsetstring.charAt(i)=='\r'){
                System.out.println("-------------------------------------------------------------------------------------------------------------------------------------------------------------");
                count=0;
            }

        }

    }
}

