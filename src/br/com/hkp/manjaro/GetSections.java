package br.com.hkp.manjaro;

import br.com.hkp.manjaro.Util.BadFileFormatException;
import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.downloadUrl;
import static br.com.hkp.manjaro.Util.green;
import static br.com.hkp.manjaro.Util.numberOfPages;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.defaultColor;
import static br.com.hkp.manjaro.Util.PREFIX;
import static br.com.hkp.manjaro.Util.cyan;
import static br.com.hkp.manjaro.Util.downloadUrl2Pathname;
import static br.com.hkp.manjaro.Util.downloadUrlMakingDirs;
import static br.com.hkp.manjaro.Util.extractFilenameFromUrl;
import static br.com.hkp.manjaro.Util.setTerminalColors;
import static br.com.hkp.manjaro.Util.showOpenMessage;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Baixa os arquivos HTML de secao e subsecao. Alem de index.php e portal.php
 * 
 * @since 13 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class GetSections
{
    /*
    Regexp para localizar paginas de secao
    */
    private static final Pattern REG_SECTION =
        Pattern.compile
        (
            "<th colspan=\"3\"><a href=\"(https:\\/\\/" +
            "manjaro-linux\\.com\\.br\\/forum\\/.+?\\/)\">"
        );
    /*
    Regexp para localizar url de paginas de subsecao
    */
    private static final Pattern REG_SUBSECTION = 
        Pattern.compile
        (
            "<h4><a href=\"(https:\\/\\/manjaro-linux\\.com\\.br\\/forum" +
            "\\/.+?\\/)\" class=\"forumtitle\">"
        );
    
    /*[01]---------------------------------------------------------------------
                  Baixa todas as paginas de subsecao do forum
    -------------------------------------------------------------------------*/
    private static void getSubsection
    (
        final String subSectionUrl, //A 1a pag. de uma subsecao: "page0.html"
        final String subSectionName //O nome da subsecao
    )
        throws IOException 
    {
        int i = 0; int lastPageNumber = 0; 
        
        System.out.println(defaultColor + "|  |");

        while (i <= lastPageNumber)
        {
            String pageNumber = "page" + i + ".html";
            
            String pathname = DOWNLOAD_DIR + '/' + subSectionName + pageNumber;
            
            String url = subSectionUrl + pageNumber;

          
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
      Baixa os arquivos portal.php, index.php, todas as paginas de secao e 
      todas de subsecao do forum Manjaro-linux.
    -------------------------------------------------------------------------*/
    private static void getSection()
    {
        try
        {
            if(!downloadUrlMakingDirs("moonmoon/index.php")) 
                throw new IOException("Download fail: moonmoon/index.php");
            
            new File(DOWNLOAD_DIR + "/moonmoon/index.php").
            renameTo(new File(DOWNLOAD_DIR + "/moonmoon/index.html"));
           
            System.out.println(PREFIX + "moonmoon/index.php");
            
            if(!downloadUrl(PREFIX + "kb.php", DOWNLOAD_DIR)) 
                throw new IOException("Download fail: " + PREFIX + "kb.php");
            
            new File(DOWNLOAD_DIR + "/kb.php").
            renameTo(new File(DOWNLOAD_DIR + "/kb.html"));
           
            System.out.println(PREFIX + "kb.php");
                   
            if(!downloadUrl(PREFIX + "portal.php", DOWNLOAD_DIR)) 
                throw new IOException("Download fail: " + PREFIX + "portal.php");
            
            new File(DOWNLOAD_DIR + "/portal.php").
            renameTo(new File(DOWNLOAD_DIR + "/portal.html"));
           
            System.out.println(PREFIX + "portal.php");
            
            if(!downloadUrl(PREFIX + "index.php", DOWNLOAD_DIR)) 
                throw new IOException("Download fail: " + PREFIX + "index.php)");
             
            new File(DOWNLOAD_DIR + "/index.php").
            renameTo(new File(DOWNLOAD_DIR + "/index.html"));
            
            System.out.println(PREFIX + "index.php\n");
            
            String indexContent = readTextFile(DOWNLOAD_DIR + "/index.php");
            
            Matcher section = REG_SECTION.matcher(indexContent);
            
            while (section.find())
            {
                String url = section.group(1);
                
                String pathname =  
                    DOWNLOAD_DIR + '/' + 
                    extractFilenameFromUrl(url.substring(0, url.length() - 1)) +
                    ".html";
                
                System.out.println(defaultColor + "+ " + url);
               
                if(!downloadUrl2Pathname(url , pathname)) 
                    throw new IOException("Download fail: " + url);
                
                String sectionContent = readTextFile(pathname);
                
                Matcher subsection = REG_SUBSECTION.matcher(sectionContent);
                
                System.out.println(defaultColor + "|");
                
                while (subsection.find())
                {
                    String subSectionUrl = subsection.group(1);
                    
                    String subSectionName =  
                        extractFilenameFromUrl
                        (
                            subSectionUrl.substring(0, subSectionUrl.length()-1)
                        ) ;       
                    
                    System.out.println(defaultColor + "|  * " + cyan + subSectionUrl);
                                            
                    getSubsection(subSectionUrl, subSectionName);
                }//while
                
                System.out.println(defaultColor + "|  =");
                
            }//while
           
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
        
        showOpenMessage("getSectionsMsg.txt");
             
        try
        {
            Thread.sleep(5000);
        } 
        catch (InterruptedException e){}
        
        File dir = new File(DOWNLOAD_DIR); dir.mkdirs();
        
        getSection();
    }//main()
    
}//classe GetSections