package br.com.hkp.manjaro;

import br.com.hkp.manjaro.Util.BadFileFormatException;
import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.PREFIX;
import static br.com.hkp.manjaro.Util.downloadUrl;
import static br.com.hkp.manjaro.Util.numberOfPages;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.setTerminalColors;
import static br.com.hkp.manjaro.Util.showOpenMessage;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import java.io.File;
import java.io.IOException;


/******************************************************************************
 * Baixa todos os arquivos HTML de topicos.
 * 
 * @since 13 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class GetTopics
{
    /*[01]---------------------------------------------------------------------
          Baixa todos os HTMLs de paginas de topicos do forum Manjaro-linux
     -------------------------------------------------------------------------*/
    private static void getTopics(final int start) throws IOException 
    {
        for(int i = start; i <= 6400; i++)
        {
            int j = 0; int lastPageNumber = 0; 

            while (j <= lastPageNumber)
            {
                String filename = 
                    "-t" + i + ((j == 0)? "" : ("-" + j)) + ".html";

                String url = PREFIX + filename;

                System.out.println(url);
                
                String fileContent = ""; 
               
                if (!downloadUrl(url, DOWNLOAD_DIR)) break;

                if (j == 0)
                {
                    fileContent = 
                        readTextFile(DOWNLOAD_DIR + '/' + filename);
                    try
                    {
                        lastPageNumber = numberOfPages(fileContent) * 10;
                    }
                    catch (BadFileFormatException e)
                    {
                        e.errMessage(fileContent, url);
             
                        break;
                    }
                }//if

                j += 10; 
   
            }//while
            
        }//for
        
    }//getTopics()
    
    /*[02]---------------------------------------------------------------------
    
    -------------------------------------------------------------------------*/
    public static void main(String[] args)
    {
        try
        {  
            setTerminalColors();//Em sistemas linux coloriza a saida no terminal
            
            showOpenMessage("getTopicsMsg.txt");//Exibe instrucoes do programa
            
            try
            {
                Thread.sleep(10000);//Da um tempo pro usuario ler as instrucoes
            } 
            catch (InterruptedException e){}
          
            /*
            Cria o diretorio para onde serao baixados os arquivos se ainda nao
            existir
            */
            File dir = new File(DOWNLOAD_DIR); dir.mkdirs();
            
            int start = 545;//Inicia o download por default no topico 545
            
            /*
            A menos que o usuario passe paramentro na linha de comando para 
            iniciar o download a partir de outro topico.
            */
            if (args.length > 0)
            {
                try
                {
                    start = Integer.valueOf(args[0]);
                }
                catch (NumberFormatException e)
                {
                    systemErrPrintln(e + " Iniciando no topico -t" + start);
                }
            }
           
            getTopics(start);//Baixa todos os htmls com paginas de topicos.
            
        } 
        catch (IOException e)
        {
            systemErrPrintln(e.toString());
        }
    }//main()
   
}//classe GetTopics
