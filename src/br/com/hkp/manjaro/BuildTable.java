package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.writeTextFile;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author 
 */
public class BuildTable
{
    private static final Pattern ARTICLE = 
        Pattern.compile("<h3 class=\"first\"><a href=\"	\">(.+?)<\\/a><\\/h3>");
    
    private static final String SCRIPT =  "<script [^§]+?<\\/script>";
    
    private static final TreeMap<String, String> treeMap = new TreeMap<>();
    
    private static void build() throws IOException
    {
        File dir = new File(DOWNLOAD_DIR);
        
        File[] listFiles = dir.listFiles();
        
        for(File htmlFile: listFiles)
        {
            String filename = htmlFile.getName();
            
            if (!filename.startsWith("a=")) continue;
            
            String content = readTextFile(htmlFile);
            
            content =
                content.replace
                (
                    "<img src=\"images/manjaro_logo.png\" " +
                    "alt=\"logo\"><img src=\"images/manjaro_logo.png\" " +
                    "alt=\"logo\">", 
                    "<img src=\"images/manjaro_logo.png\" alt=\"logo\">"
                );
            
            content = content.replaceAll(SCRIPT, "");
            
            Matcher m = ARTICLE.matcher(content);
            
            if (m.find()) treeMap.put(m.group(1), filename);
            
            writeTextFile(htmlFile, content);
           
        }
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("<table>\n");
        
        for(String article: treeMap.keySet())
        {
            
            sb.append("<tr><th><a href=\"").append(treeMap.get(article)).
               append("\">").append(article).append("</a></th></tr>\n");
        }
        
        sb.append("</table>");
        
        System.out.println(sb.toString());
        
    }
    
    public static void main(String[] args) throws IOException
    {
        build();
    }
    
}//BuildTable
