# nbk

Esta é a implementação do desafio da Nubank:

- Implementar uma API RESTFul para manipulação de um grafo em memória
- Adicionar um método que calcule a centralidade do grafo

Foi utilizada a linguagem Clojure.


## Adicionar vertices ao grafo atual via arquivo em disco

**[POST]** `/graph/edges `

###Parametro:
  path: "caminho/do/arquivo/em.disco"

  Indique o caminho de um arquivo em disco contendo todos as ligações entre vértices, uma por linha.

  Exemplo do arquivo:

  1 2

  2 3

  1 3

###Retorno:
  A nova versão do grafo, já processada.


## Adicionar vertices ao grafo atual via JSON

**[PUT]** `/graph/edges`

###Parametro:
  Um RAW com o JSON indicando a ligação entre cada vertices.

  Exemplo de parametro: {v1:1, v2:4}

  No exemplo acima foi indicado que o vértice 1 APONTA para vértice 4, e vise-versa ou seja 1 <=> 4.

  Por tanto: faça quantas chamadas forem necessárias para criar a indicação completa de todas as
  ligações entre vértices.

###Retorno:
  A nova versão do grafo, já processada.


## Obter o grafo atual

**[GET]** `/graph`

###Retorno:
  Um mapa que representa o grafo atual.


## Obter o vertice de maior centralidade

**[GET]** `/graph/closest`

###Retorno:
  O vértice de maior centralidade


## Apaga o conteúdo do grafo

**[PUT]** `/graph/reset`

###Retorna:
  Um Objeto JSON vazio: {}

## Testes

Os testes estão no namespace nbk.graph-test. Execute no terminal o seguinte comando:

	lein midje


## Servidor Http

Para subir o servidor http, abra o repl:

    lein repl

E depois execute a função go:

     user=> (go)
     #<SystemMap>





