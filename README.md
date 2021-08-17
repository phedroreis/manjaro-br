manjaro-br
==========

Projeto para desenvolver uma ferramenta para construir a cópia estática do Fórum [Manjaro Brasil](www.manjaro-linux.com.br).

### Os Programas

Consistem de algumas simples aplicações Java que, se executadas em uma determinada ordem, produzem uma cópia localmente navegável do fórum Manjaro-BR. 

* GetTopics.jar
* GetSections.jar
* GetSubforuns.jar
* GetArticles.jar
* GetFiles.jar
* Build.jar
* BuildKbPage.jar

_Estes programas não podem ser usados para copiar algum outro fórum. Foram desenvolvidos especificamente para o Manjaro-BR. No entanto sinta-se livre e à vontade para estudar o código e adapta-lo para qualquer uso que lhe parecer conveniente. Desde que respeitando os termos da licença MIT aqui concedida._

Todos os programas listados foram projetados para serem executados pelo e em um terminal. A saída e a interação com o usuário (quando e se necessária) se dá via terminal. 

Para executar qualquer destes programas apenas use o comando:

<kbd><code>**$ java -jar <nomeDoPrograma.jar>**</code></kbd>

Obs: Estes programas podem ser executados em qualquer versão ou distruibuição linux, 32 ou 64 bits, e também em sistemas MacOS. Porém, devido à particularidades na forma como é permitido nomear arquivos e diretórios em sistemas Windows, alguns destes programas podem não produzir o resultado esperado quando executados em plataformas Microsoft.

### Instruções

Apenas copie os arquivos listados para qualquer diretório, abra um terminal neste diretório e execute o programa com <kbd><code>**$ java -jar <nomeDoPrograma.jar>**</code></kbd>

Para que uma cópia estática do Manjaro-BR seja produzida ao final do processo, é obrigatório que os programas sejam executados **exatamente** na ordem indicada a seguir:

1. GetTopics.jar (java -jar GetTopics.jar)
2. GetSections.jar (java -jar GetSections.jar)
3. GetSubforuns.jar (java -jar GetSubforuns.jar)
4. GetArticles.jar (java -jar GetArticles.jar)
5. GetFiles.jar (java -jar GetFiles.jar)
6. Build.jar (java -jar Build.jar)
7. BuildKdPage.jar (java -jar BuildKdPage.jar)

##### GetTopics.jar

Este deve ser o primeiro programa a ser executado e irá baixar todos os arquivos HTML referentes à páginas de tópicos. Se ainda não existir, o programa irá criar no diretório onde estiver sendo executado, um subdiretório de nome manjaro-linux.com.br, onde será construída a cópia estática do Manjaro-BR.

Dependendo da conexão este processo pode levar cerca de uma hora. É normal que ele exiba a mensagem de erro <samp>File not Found</samp> quando estiver tentando baixar um tópico que o programa presuma que exista. Não há problema nisso, apenas deixe seguir o download.

  A mensagem de erro <samp>Bad File</samp> também irá aparecer eventualmente. Apenas significa que ele tentou baixar um tópico reservado, o qual era preciso ser um usuário registrado para obter acesso.

No entanto se ocorrer algum erro de IO no download ou na gravação do arquivo, o programa irá repetir a operação 3 vezes e se não houver sucesso solicitará que o usuário decida tentar novamente ou abortar o download daquele arquivo.

Para estes casos é aconselhável abortar o programa se o arquivo não puder ser baixado e tentar executar novamente o GetTopics mais tarde. Páginas de tópicos não baixadas por falhas na conexão significam que estes tópicos não estarão presentes para consulta no simulacro estático do fórum que será gerado.

Mas observe que este programa numera **cada tópico** e segue uma ordem numérica crescente. Caso o GetTopics seja interrompido durante o download de um tópico número XXX, é possível reiniciar posteriormente o download exatamente a partir desse tópico que falhou. Não perdendo assim os arquivos que já foram baixados.

Para isso, quando reiniciar o programa, apenas use na linha de comando:

  <kbd><code>**$ java -jar GetTopics.jar [numDoTópico]**</code></kbd>

Por exemplo:

Se o download falhou ao tentar baixar o tópico 3251, na próxima vez rode com java -jar GetTopics.jar 3251

  Lembrando que isto **NÃO SE APLICA** quando mensagens de erro <samp>File Not Found</samp> ou <samp>Bad File</samp> forem emitidas. Nestas situações deve-se permitir ao programa continuar.

GetTopics, por padrão, inicia tentando baixar o tópico 540 e prossegue até o tópico 6400. Porém muitos destes tópicos não existirão ou podem ser reservados e não acessíveis. Não há erro nisso, apenas deixe prosseguir até o final.

##### GetSections.jar

Deve ser executado após o GetTopics e este programa irá baixar todos os arquivos HTML referentes à páginas de seção e de subseção.

Este download é bem mais rápido, mas nesse caso **TODAS** as páginas que o programa tentar baixar devem ser baixadas. Se ocorrer alguma falha durante o download de algum arquivo GetSections.jar deverá ser executado novamente.

Enquanto GetTopics apenas presume a existência de tópicos com índices no intervalo 540 até 6400, GetSections **SABE** quais páginas de seção e subseção existem no fórum. Porque obteve esta informação analisando o arquivo index.php que ele mesmo trata de baixar do servidor.

Poranto todas estas páginas são necessárias à cópia estática e todas devem ser obtidas do servidor.

##### GetSubforuns.jar

É semelhante ao GetSections.jar e deve ser executado após o sucesso daquele. 

Obtém as páginas referentes ao subforuns.

##### GetArticles.jar

Obtém os arquivos referentes aos artigos do Knowledge Base.

##### GetFiles.jar

Este programa é muito importante e só pode ser executado após o sucesso na execução dos anteriores.

Ele analisa os arquivos que foram obtidos pelos programas precedentes e tenta baixar todo e qualquer arquivo no servidor do fórum que faça parte de uma página a ser reconstruída na cópia estática.

No entanto é possível que alguns destes arquivos, por alguma razão, já não existam mais no servidor embora estejam linkados ou referenciados em algum HTML enviado pelo sistema do fórum.

Não tem importância, no caso de falha no download de alguns arquivos pelo GetFiles.jar, isto não impedirá que a cópia estática funcione. Deixe rodar até o final mesmo que falhas por ventura ocorram.

##### Build.jar

Agora que você tem todos os arquivos, é hora de construir a cópia estática editando os HTMLs que foram baixados. Essa é a tarefa do Build.jar.

  Apenas digite <kbd><code>**$ java -jar Build.jar**</code></kbd> e deixe o programa fazer seu trabalho.

##### BuildKbPage.jar

Estamos quase lá! Agora falta construir a página que lista os links para os artigos do Knowledege Base.

Execute <kbd><code>**$ java -jar BuildKdPage.jar**</code></kbd>

### Um Último Procedimento

Neste repositório a uma pasta nomeada forum, com arquivos e diretórios que tiveram que ser obtidos por outros meios (por exemplo: arquivos que eram gerados por código PhP).

Esta pasta precisa ser copiada para dentro da pasta manjaro-linux.com.br, criada pelos programas previamente executados no diretório onde foram executados. Ela deve ser mesclada com a pasta forum já existente nesse diretório.

E zé fini! A cópia estática existe na sua máquina local!

Para começar a navegar procure pelo arquivo portal.html no diretório forum, ou então pelo arquivo index.html neste mesmo diretório e abra em seu navegador.

Apenas isso.

### Observações

Os programas que podem ser obtidos neste repo são aplicações Java e necessitam que o sistema tenha uma JRE compatível instalada. 

Na maioria das distribuições Linux o OpenJDK é instalado por padrão. Mas pode ser que sua versão do OpenJDK não seja totalmente compatível, pois para codificar e compilar estes programas foi usado o Oracle JDK 15.

Nesse caso tente atualizar seu OpenJDK e se o problema persistir será necessário obter o Oracle JDK. Não sei como fazer isso no Manjaro, mas em sistemas da família Debian os passos são:

<kbd>**$ sudo add-apt -repository ppa:linuxuprising/java**</kbd>
  
<kbd>**$ sudo apt update**</kbd>

<kbd>**$ sudo apt install oracle-java15-installer**</kbd> 


E se quiser, depois disso, pode desinstalar o OpenJDK.

<kbd>**$ sudo apt-get remove --purge openjdk-***</kbd>

