package br.com.hkp.manjaro;

import static br.com.hkp.manjaro.Util.DOWNLOAD_DIR;
import br.com.hkp.manjaro.Util.HtmlFilter;
import static br.com.hkp.manjaro.Util.readTextFile;
import static br.com.hkp.manjaro.Util.systemErrPrintln;
import static br.com.hkp.manjaro.Util.writeTextFile;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/******************************************************************************
 * Constroi a copia estatica alterando os arquivos HTML baixados do forum 
 * Manjaro-BR.
 * 
 * @since 14 de março de 2021 v1.0
 * @version 1.0
 * @author "Pedro Reis"
 *****************************************************************************/
public class Build
{
    /*-----------------------------------------------------------------------*
    As expressoes regulares localizam padroes de strings nos arquivos HTML das
    paginas do forum. 
    -------------------------------------------------------------------------*/
    /*
    Localiza atributos herf
    */
    private static final Pattern HREF = 
        Pattern.compile("href=\"(.+?)\"");
    
    /*
    Localiza atributos href e action
    */
    private static final Pattern HREFACTION = 
        Pattern.compile("(href|action)=\"(.+?)\"");
    
    /*
    Localiza links para posts
    */
    private static final Pattern POST =
        Pattern.compile("-t\\d+(-\\d+)*\\.html#(p\\d+|wrapper)");
    /*
    Localiza links para topicos
    */
    private static final Pattern TOPIC =
        Pattern.compile("-t\\d+(-\\d+)*\\.html");
    /*
    Localiza uma declaracao de variaval no script JS que abre um window.alert
    para permitir ao usuario digitar o n. da pag. para a qual quer ir.
    */
    private static final Pattern VAR_BASE_URL =
        Pattern.compile("var base_url = '(.+?)\\/'");
    
    private static final HashSet<String> SECTIONS_FILES = new HashSet<>(12);
   
    /*[01]---------------------------------------------------------------------
       O metodo que edita cada um dos arquivos HTML obtidos do forum para que
       se comportem como uma copia estatica do forum original.
    -------------------------------------------------------------------------*/    
    private static void editHtml()
    {
        SECTIONS_FILES.add("normatividade.html");
        SECTIONS_FILES.add("geral.html");
        SECTIONS_FILES.add("mao-na-massa.html");
        SECTIONS_FILES.add("edicoes.html");
        SECTIONS_FILES.add("diversos.html");
        SECTIONS_FILES.add("manjaro-official.html");
        
        /*
        A estrutura mapeia urls de links que serao editados. Cada entrada nesse
        map e um par key/value onde key eh a url original e value recebe e a url
        modificada que irah substituir a original
        */
        HashMap<String, String> HREF_MAP = new HashMap<>();
        
        /*
        Obtem o diretorio onde estao os HTMLs que serao editados
        */
        File downloadDir = new File(DOWNLOAD_DIR);
        
        /*
        Um array com todos os HTMLs do diretorio
        */
        File[] listFiles = downloadDir.listFiles(new HtmlFilter());
        
        /*
        Um array com o nome de todos os arquivos que sao paginas de subsecoes e
        subforums
        */
        String[] sections = downloadDir.list(new FileFilter());
        
        /*
        Converte os nomes destes arquivos para a string que referencia estas 
        subsecoes e subforums nos arquivos HTML originais baixados do forum.
        
        Este array servirah para converter estes links para os nomes reais com
        os quais estes arquivos (paginas de subsecao e subforum) estao gravados
        no disco.
        */
        for(int i = 0; i < sections.length; i++)
        {
            sections[i] = "href=\"" + sections[i].replace("page0.html", "/");
        }//for
        
        /*
        O loop processa todos os HTMLs do diretorio para onde foram baixados. 
        Todas as paginas de topicos, secao, subsecao, subforum, etc...
        */
        for(File htmlFile: listFiles)
        {
            /*
            Obtem nome do arquivo que sera editado no corpo do loop for
            */
            String filename = htmlFile.getName();
            
            System.out.println("Processando: " + filename + "\n");
            
            String htmlContent;
            
            /*
            Le o conteudo de um arquivo HTML para dentro da String htmlContent
            
            Esta string serah sucessivamente editada ao longo de uma iteracao
            deste loop, ate se tornar o conteudo de um arquivo que, 
            estaticamente, simula o arquivo original gerado dinamicamente pelo
            sistema do forum Manjaro-BR.
            
            Ao final do loop, htmlContent eh gravada de volta para o arquivo,
            produzindo assim a versao do arquivo que ira funcionar no conjunto
            estatico.
            */
            try { htmlContent = readTextFile(htmlFile); }
            catch(IOException e) { systemErrPrintln(e.getMessage()); break; }
           
            /*
            Urls que apontam para https:\\manjaro-linux.com.br\forum\ vao passar
            a apontar para o diretorio corrente na copia local. Ou seja, o
            diretorio onde serao armazenados todos os HTMLs baixados do forum.
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br\\/forum\\/", 
                    ""
                );
            
            /*
            Url relativa ao diretorio corrente no servidor tambem passa a 
            apontar para onde estao os HTMLs na copia local
            */
            htmlContent = htmlContent.replaceAll("=\" *./", "=\"");
            
            
            /*
            Urls que apontam para o diretorio pai de /forum no servidor vao 
            passar a apontar para o pai do diretorio corrente na copia local.
            /forum serah o diretorio corrente dos HTMLs na copia local.
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br\\/", 
                    "../"
                );
            
            /*
            Urls que apontam para a raiz do servidor passam a apontar para a
            pagina de entrada do forum portal.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "https?:\\/\\/(www\\.)?manjaro-linux\\.com\\.br", 
                    "portal.html"
                );
            
            /*
            Deleta o prefixo dos nomes de arquivos para avatares
            */
            htmlContent = htmlContent.replace("file.php?avatar=", "");
            
            /*
            Deleta as sid das URLs. Evita que o mesmo arquivo seja referenciado
            com mais de um nome.
            */
            htmlContent = 
                htmlContent.replaceAll("&amp;sid=[a-f0-9]+?\"", "\"");

            htmlContent = 
                htmlContent.replaceAll("\\?sid=[a-f0-9]+?\"", "\"");
            
            /*
            Renomeia o link para o arquivo CSS principal que eh enviado pelo 
            servidor por meio de uma requisicao PHP
            */
            htmlContent = 
                htmlContent.replaceAll("style\\.php\\?.+?\"", "style.css\"");
           
            /*
            Substitui kb.php?etc... por kb.html somente. Este arquivo sera 
            renomeado na copia local para kb.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "href=\"kb\\.php\\?.+?\"", 
                    "href=\"kb.html\""
                ).replace("kb.php", "kb.html");
            
            /*
            Renomeia referencias a portal.php e index.php para os nomes
            que estes arquivos receberao na copia local
            */
            htmlContent = 
                htmlContent.replace("portal.php", "portal.html").
                            replace("index.php", "index.html");
            /*
            Links para pagina de grupos de usuarios redirecionado para 
            default.html
            */
            htmlContent =
                htmlContent.replaceAll("\\w+-g\\d+\\.html", "default.html");
           
            /*
            Links para paginas de membros do forum sao redirecionados para
            default.html
            */
            htmlContent = 
                htmlContent.
                replaceAll("\"member\\/.+?\\/\"", "\"default.html\"");
            
            /*
            Substitui postxxx.html#pxxx por default.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "href=\"post\\d+\\.html.+?\"", 
                    "href=\"default.html\""
                );
            
            /*
            Substitui links para funcoes PHP por link para default.html
            */
            htmlContent = 
                htmlContent.replaceAll
                (
                    "(thankslist|faq|viewtopic|viewforum|" +
                    "ucp|form|search|posting|feed)\\.php.*?\"", 
                    "default.html\""
                );
            
            /*
            Edita links para secoes e subsecoes
            */
            htmlContent = 
                htmlContent.replace
                (
                    "/page", 
                    "page"
                );  
            
            
            /*
            Edita links para posts
            */
            Matcher m = HREF.matcher(htmlContent);

            while (m.find())
            {
                String url = m.group(1);

                if (url.contains("http")) continue;

                Matcher m2 = POST.matcher(url);

                if (m2.find())
                {
                    String post = m2.group();
                    HREF_MAP.put(url, post);
                }
            }//while

            for(String url: HREF_MAP.keySet())
            {
                htmlContent = htmlContent.replace(url, HREF_MAP.get(url));
            }//for

            /*
            Edita links para topicos
            */
            HREF_MAP = new HashMap<>(); 

            m = HREFACTION.matcher(htmlContent);

            while (m.find())
            {
                String url = m.group(2);

                if (url.contains("http")) continue;

                Matcher m2 = TOPIC.matcher(url);

                if (m2.find())
                {
                    String topic = m2.group();
                    HREF_MAP.put(url, topic);
                }
            }//while

            for(String url: HREF_MAP.keySet())
            {
                htmlContent = htmlContent.replace(url, HREF_MAP.get(url));
            }//for
         
            /*
            Ajusta os links para subsecoes e subforuns
            */
            for(String section: sections)
            {
                String firstPage = section.replace("/", "page0.html");
                htmlContent = htmlContent.replace(section, firstPage);
            }
            
            /*
            Converte todos os links para topicos a uma mesma sintaxe
            */
            htmlContent = htmlContent.replace("href=\"topic", "href=\"-t"); 
            
            /*
            Normaliza links para posts
            */
            htmlContent = 
                htmlContent.replaceAll("\\.html&amp;p=\\d+#p", ".html#p");
            
            /*
            Corrige o script responsavel por abrir uma caixa de dialogo que 
            permite ao usuario digitar o num. da pag. para a qual quer ir. Mas
            apenas em paginas de subforums e subsecoes.
            */
            if (filename.contains("page"))
            {
                m = VAR_BASE_URL.matcher(htmlContent);
                if (m.find())
                {
                    String cssVar = m.group();
                    String sectionName = m.group(1);
                    htmlContent = 
                        htmlContent.
                        replace(cssVar, "var base_url = './'");
                    htmlContent = 
                        htmlContent.replace
                        (
                            "var seo_static_pagination = 'page';", 
                            "var seo_static_pagination = '" + 
                            sectionName + "page';"        
                        );
                }
            }else if (filename.equals("index.html"))
            /*
            Corrige links para paginas de secao no arquivo index.html
            */
            {
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"normatividade/", "href=\"normatividade.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"geral/", "href=\"geral.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"mao-na-massa/", "href=\"mao-na-massa.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"edicoes/", "href=\"edicoes.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"diversos/", "href=\"diversos.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "href=\"manjaro-official/", 
                        "href=\"manjaro-official.html"
                    );
                htmlContent = 
                    htmlContent.replace
                    (
                        "<a href=\"manjaro-forumspage0.html\"", 
                        "<a href=\"https://forum.manjaro.org/\""
                    );
            }else if (filename.equals("manjaro-official.html"))
            /*
            O link na pagina de secao manjaro-official deve apontar para 
            forum.manjaro.org
            */
            {
                htmlContent = 
                    htmlContent.replace
                    (
                        "<a href=\"manjaro-forumspage0.html\"", 
                        "<a href=\"https://forum.manjaro.org/\""
                    );
                
            }//if-else
            
            /*
            Corrige auto-link nas paginas de secao
            */
            if (SECTIONS_FILES.contains(filename))
            {
                htmlContent = 
                    htmlContent.replace
                    (
                        "<a href=\"" + filename.replace(".html", "/"), 
                        "<a href=\"" + filename
                    );
                
            }//if
            
            /*
            Insere o logo do Manjaro BR no alto de cada pagina
            */
            htmlContent = 
                htmlContent.replace
                (
                    "<div id=\"site-description\">", 
                    "<div id=\"site-description\"><img src=" +
                    "\"images/manjaro_logo.png\" alt=\"logo\">"
                );

            /*
            Grava o conteudo editado do arquivo de volta para o proprio arquivo
            */
            try { writeTextFile(htmlFile, htmlContent); }
            catch(IOException e) { systemErrPrintln(e.getMessage()); break; }
            
        }//for
        
        
    }//editHtml()
    
    /*[02]---------------------------------------------------------------------
      
    -------------------------------------------------------------------------*/ 
    /**
     * Executa o programa.
     * 
     * @param args Argumentos nao utilizados.
     */
    public static void main(String[] args)
    {
        editHtml();
    }//main()
    
   
    /*-************************************************************************
     Classe interna. Um filtro que seleciona apenas os nomes de arquivos que 
     sejam as primeiras paginas de subsecoes ou subforums.
    ***************************************************************************/ 
    private static class FileFilter implements FilenameFilter
    {
        @Override
        public boolean accept(File dir, String filename)
        {
            return filename.contains("page0.html");
        }
    }//classe FileFilter
      
}//classe Build
