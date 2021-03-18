package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.PREFIX;
import static br.com.hkp.manjaro.Util.downloadUrl;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import java.io.File;
import java.io.IOException;


/******************************************************************************
 * Baixa todos os arquivos HTML de artigos.
 * 
 * @since 18 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class GetArticles
{
    /*[01]---------------------------------------------------------------------
                        Baixa todos os HTMLs de artigos
     -------------------------------------------------------------------------*/
    private static void getArticles() throws IOException 
    {
        for(int i = 43; i <= 150; i++)
        {
           
                String filename =  "kb.php?a=" + i;
   
                String url = PREFIX + filename;

                System.out.println(url);
                            
                downloadUrl(url, DOWNLOAD_DIR);
                
                File file = new File(DOWNLOAD_DIR + '/' + filename);
                
                String content = readTextFile(file);
                
                if (content.contains("nenhum artigo com esse ID.</p>"))
                    file.delete();
                else
                    file.renameTo(new File(DOWNLOAD_DIR + "/a=" + i +".html"));
        }//for
        
    }//getArticles()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static void main(String[] args)
    {
        try
        {  
            /*
            Cria o diretorio para onde serao baixados os arquivos se ainda nao
            existir
            */
            File dir = new File(DOWNLOAD_DIR); dir.mkdirs();
                     
            getArticles();
            
        } 
        catch (IOException e)
        {
            systemErrPrintln(e.toString());
        }
    }//main()
   
}//classe GetArticles
