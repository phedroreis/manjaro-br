package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import static br.com.hkp.manjaro.Util.writeTextFile;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Corrige o problema do bug em links para posts apontados como aqueles que 
 * resolveram o problema que motivou a criacao de um topico.
 * 
 * @since 21 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class BugFix
{
    private static final Pattern TOPIC =
        Pattern.compile
        (
            "<a href=\"(-t\\d+\\.html)\" class=\"topictitle\">.+?</a>" +
            "&nbsp;&nbsp;<a href=\"(-t\\d+\\.html.*?)\" class=\"topictitle\"" +
            " style=\"color: #FF0000\">\\[Resolvido\\]</a>"
        );
    
    private static final String TARGET_LINK =
        "<a href=\"default.html\" style=\"color: #FF0000\">[Resolvido]</a></h2>";
    
    /*[01]---------------------------------------------------------------------
 
    -------------------------------------------------------------------------*/    
    private static void fix() throws IOException
    {
        File downloadDir = new File(DOWNLOAD_DIR);
        
        File[] fileList = downloadDir.listFiles(new SubsectionFilter());
        
        for(File subSectionFile: fileList)
        {
            System.out.println
            (
                "Processando topicos de: " + subSectionFile.getName()
            );
            
            String subSectionContent = readTextFile(subSectionFile);
            
            Matcher m = TOPIC.matcher(subSectionContent);
            
            while (m.find())
            {
                String topicName = m.group(1);
                String link = m.group(2);
                
                System.out.println("\n  " + m.group());
                
                System.out.println
                (
                    "  Topico: " + topicName + " - Link a ser editado: " +
                    link + "\n"
                );
                
                File[] topicList = 
                    downloadDir.listFiles(new TopicFilter(topicName));
                
                for(File topicFile: topicList)
                {
                    System.out.print
                    (
                        "    Pag. do topico: " + topicFile.getName()
                    );
                    
                    String topicFileContent = readTextFile(topicFile);
                    
                    String newLink = TARGET_LINK.replace("default.html", link);
                    
                    System.out.println(" <-- " + newLink);
                    
                    topicFileContent = 
                        topicFileContent.replace(TARGET_LINK, newLink);
                    
                    writeTextFile(topicFile, topicFileContent);
                }//for
                  
            }//while
            
            System.out.println("");
            
        }//for
        
    }//fix()
    
    /*[02]---------------------------------------------------------------------
 
    -------------------------------------------------------------------------*/  
    /**
     * Executa o programa.
     * 
     * @param args n/a
     */
    public static void main(String[] args)
    {
        try
        {
            fix();
        } 
        catch (IOException ex)
        {
            systemErrPrintln(ex.toString());
        }
        
    }//main()
    
   /***************************************************************************
    *    Classe interna. Um filtro que seleciona arquivos HTML com paginas
    *    de subsecao.
    **************************************************************************/
    private static final class SubsectionFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename)
        {
           return filename.contains("page");
        }
    }//classe SubsectionFilter
    
   /***************************************************************************
    *    Classe interna. Retorna se a pagina pertence ao topico que foi 
    *    atribuido a classe no construtor
    **************************************************************************/
    private static final class TopicFilter implements FilenameFilter
    {
        private final String TOPIC_NAME;
        
        public TopicFilter(final String topicName)
        {
            TOPIC_NAME = topicName.replace(".html", "");
        }//construtor
        
        @Override
        public boolean accept(File dir, String filename)
        {
           return filename.contains(TOPIC_NAME);
        }
    }//classe TopicFilter
   
}//classe BugFix
