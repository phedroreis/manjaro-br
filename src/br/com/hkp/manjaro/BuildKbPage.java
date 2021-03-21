package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import static br.com.hkp.manjaro.Util.getTxtResource;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import static br.com.hkp.manjaro.Util.writeTextFile;
import java.io.File;
import java.io.IOException;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Cria o arquivo da pagina do Knowledge Base e edita os arquivos HTML de 
 * artigos.
 * 
 * @since 18 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public final class BuildKbPage
{
    /*
    Localiza o nome do artigo
    */
    private static final Pattern ARTICLE = 
        Pattern.compile("<h3 class=\"first\"><a href=\"	\">(.+?)<\\/a><\\/h3>");
    /*
    Localiza todos os scripts da pagina. Scripts serao deletados.
    */
    private static final String SCRIPT =  "<script [^§]+?<\\/script>";
    
    /*
    Serve para editar o nome do arquivo que eh apresentado na pagina para ser 
    utilizado com um link externo para este artigo.
    */
    private static final Pattern EXTERNAL_LINK = 
            Pattern.compile("<code>kb.html\\?a=(\\d+)<\\/code>");
    
    /*
    Par key/value <nomeDoArtigo/ArquivoDoArtigo>
    */
    private static final TreeMap<String, String> treeMap = new TreeMap<>();
    
    /*[01]---------------------------------------------------------------------
       Constroi o arquivo kb.html e edita os HTMLs dos arquivos de artigos
    -------------------------------------------------------------------------*/
    private static void build() throws IOException
    {
        File dir = new File(DOWNLOAD_DIR);
        
        File[] listFiles = dir.listFiles();
        
        for(File htmlFile: listFiles)
        {
            String filename = htmlFile.getName();
            
            if (!filename.startsWith("a=")) continue;
            
            String content = readTextFile(htmlFile);
            /*
            Corrige um erro que faz aparecer o logo Manjaro 2 vezes na pagina
            */
            content =
                content.replace
                (
                    "<img src=\"images/manjaro_logo.png\" " +
                    "alt=\"logo\"><img src=\"images/manjaro_logo.png\" " +
                    "alt=\"logo\">", 
                    "<img src=\"images/manjaro_logo.png\" alt=\"logo\">"
                );
            
            /*
            Deleta codigos desnecessarios de scripts na pagina
            */
            content = content.replaceAll(SCRIPT, "");
            
            /*
            Ajusta o nome do arquivo para ser usado como link externo para o 
            artigo
            */
            Matcher m = EXTERNAL_LINK.matcher(content);
            
            if (m.find())
                content = 
                    content.replace
                    (
                        m.group(), "<code>a=" + m.group(1) + ".html</code>"
                    );
            
            m = ARTICLE.matcher(content);
            
            if (m.find()) treeMap.put(m.group(1), filename);
            
            /*
            Grava o novo conteudo do arquivo de volta para o arquivo original
            */
            writeTextFile(htmlFile, content);
           
        }//for
        
        /*
        Constroi o conteudo do arquivo kb.html
        */
        StringBuilder sb = new StringBuilder();
         
        sb.append(getTxtResource("head.txt"));
        
        sb.append("<table>\n");
        
        for(String article: treeMap.keySet())
        {
            sb.append("<tr><th><a href=\"").append(treeMap.get(article)).
               append("\">").append(article).append("</a></th></tr>\n");
        }
        
        sb.append("</table>\n</body>\n</html>");
        
        /*
        Grava o arquivo kb.html
        */
        writeTextFile(new File(DOWNLOAD_DIR + "/kb.html"), sb.toString());
        
    }//build()
    
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
            build();
        }
        catch(IOException e)
        {
            systemErrPrintln(e.toString());
        }
    }//main()
    
}//classe BuildKbPage
