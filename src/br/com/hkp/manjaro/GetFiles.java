package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import br.com.hkp.manjaro.Util.HtmlFilter;
import static br.com.hkp.manjaro.Util.downloadUrl;
import static br.com.hkp.manjaro.Util.extractFilenameFromUrl;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Baixa arquivos do servidor.
 * 
 * @since 14 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class GetFiles
{
    private static final HashSet<String> DOWNLOADEDS_URLS = new HashSet<>();
    
    private static final String DOMAIN = "https://www.manjaro-linux.com.br";
    
    private static final String PREFIX1 = "\\.";
    
    private static final String PREFIX2 = 
        "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br";
    
    private static final String REGEXP =
        "((" + PREFIX1 + ")|(" + PREFIX2 + "))\\/.+";        
    
    private static final Pattern HREFSRC = 
        Pattern.compile("(href|src)=\"(.+?)\"");
    
    private static final Pattern CSS = 
        Pattern.compile("url\\((.+?)\\)");
    
    /*[01]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static boolean downloadMakingDirs(final String url) throws IOException
    {
        int lastIndexOf = url.lastIndexOf('/');
        String path;
        if (lastIndexOf != -1)
        {
            path = "./manjaro-linux.com.br" + url.substring(0, lastIndexOf);
            File dir = new File(path);
            if (!dir.exists()) dir.mkdirs();
        }
        else
            throw new IOException("URL error: " + url);
        
        String pathname = path + '/' + extractFilenameFromUrl(url);
        
        if (DOWNLOADEDS_URLS.contains(pathname))
            return true;
        else
        {
            DOWNLOADEDS_URLS.add(pathname);
            
            System.out.println("Baixando: " + DOMAIN + url);

            return downloadUrl( DOMAIN + url, path);
        }
      
    }//downloadMakingDirs()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    private static void get() throws IOException
    {
        File dir = new File(DOWNLOAD_DIR);
        
        File[] listFiles = dir.listFiles(new HtmlFilter());
        
        for(File htmlFile: listFiles)
        {
            System.out.println("\nProcessando: " + htmlFile.getName() + "\n");
            
            String htmlContent = readTextFile(htmlFile);
            
            Matcher m = HREFSRC.matcher(htmlContent);
            
            while (m.find())
            {
                String url = m.group(2);
                
                if 
                (
                    url.contains(".html") ||
                    url.contains(".php")  ||
                    url.endsWith("/")
                ) 
                    continue;
                
                if (url.matches(REGEXP))
                {
                    url = url.replace("./", "/forum/").
                              replaceAll(PREFIX2, "");
                    
                    url = url.replaceAll("\\?.*", "").
                              replaceAll("&amp;sid=[a-f0-9]+", "");
                     
                    downloadMakingDirs(url);
                 
                }//if
                 
            }//while
            
            m = CSS.matcher(htmlContent);
            
            while (m.find())
            {
                String url = m.group(1);
                
                url = url.replace("\"", "").replace("'", "");
                
                url = url.replace("./", "/forum/").
                          replaceAll(PREFIX2, "");
                
                downloadMakingDirs(url);
            }//while
      
        }//for
        
    }//get()
    
    /*[03]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static void main(String[] args)
    {
        try
        {
            get();
        }
        catch (IOException ex)
        {
            systemErrPrintln(ex.toString());
        }
    }//main()
    
}//classe GetFiles
