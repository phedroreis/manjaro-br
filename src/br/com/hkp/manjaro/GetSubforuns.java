package br.com.hkp.manjaro;

import br.com.hkp.manjaro.Util.BadFileFormatException;
import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.downloadUrl2Pathname;
import static br.com.hkp.manjaro.Util.extractFilenameFromUrl;
import static br.com.hkp.manjaro.Util.green;
import static br.com.hkp.manjaro.Util.numberOfPages;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.defaultColor;
import static br.com.hkp.manjaro.Util.setTerminalColors;
import static br.com.hkp.manjaro.Util.showOpenMessage;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Baixa os arquivos HTML de subforuns. 
 * 
 * @since 13 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class GetSubforuns
{
    /*
    Regexp para localizar url de paginas dos subforuns
    */
    private static final Pattern REG_SUBFORUM = 
        Pattern.compile
        (
            "<a href=\"(.+?\\/)\" class=\"subforum read\"" 
        );
    /*[01]---------------------------------------------------------------------
                  Baixa todas as paginas de subforum do forum
    -------------------------------------------------------------------------*/
    private static void getSubforum
    (
        final String subForumUrl, //A url de um subforum
        final String subForumName //O nome do sub-forum.
    )
        throws IOException 
    {
         
        int i = 0; int lastPageNumber = 0; 
        
        System.out.println(defaultColor + "|  |");

        while (i <= lastPageNumber)
        {
            String pageNumber = "page" + i + ".html";
            
            String pathname = DOWNLOAD_DIR + '/' + subForumName + pageNumber;

            String url = subForumUrl + pageNumber;

            System.out.println(defaultColor + "|  |   ° " + green + url);

            String fileContent = ""; 


            if (!downloadUrl2Pathname(url, pathname)) break;

            if (i == 0)
            {
                fileContent = readTextFile(pathname);
                try
                {
                    lastPageNumber = numberOfPages(fileContent) * 25;
                }
                catch (BadFileFormatException e)
                {
                    e.errMessage(fileContent, url);
                    
                    break;
                }

            }//if

            i += 25; 
            
        }//while
        
        System.out.println(defaultColor + "|  |");

    }//getSubsection()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    private static void getSubforuns()
    {
        try
        {
                
            String indexContent = readTextFile(DOWNLOAD_DIR + "/index.html");
            
            Matcher subforum = REG_SUBFORUM.matcher(indexContent);
            
            while (subforum.find())
            {
                String subForumUrl = subforum.group(1);
                   
                System.out.println(defaultColor + "+ " + subForumUrl);
                
                String subForumName =  
                    extractFilenameFromUrl
                    (
                        subForumUrl.substring(0, subForumUrl.length()-1)
                    ) ;    
  
                getSubforum(subForumUrl, subForumName);
                
            }//while
                
            System.out.println(defaultColor + "|  =");
          
        } 
        catch (IOException e)
        {
            systemErrPrintln(e.toString());
        }
        finally
        {
            System.out.print(defaultColor);
        }
        
    }//getSection()
    
    /*[03]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static void main(String[] args)
    {
        setTerminalColors();
        
        showOpenMessage("getSubforunsMsg.txt");
             
        try
        {
            Thread.sleep(5000);
        } 
        catch (InterruptedException e){}
        
        File dir = new File(DOWNLOAD_DIR); dir.mkdirs();
        
        getSubforuns();
    }//main()
    
}//classe GetSubforuns

