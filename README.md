# 📚 Book Tracker

Aplicativo Android de gerenciamento pessoal de leitura desenvolvido em Kotlin com arquitetura MVVM.

## 📖 Sobre o Projeto

O Book Tracker permite aos usuários organizar sua biblioteca pessoal, acompanhar o progresso de leitura e descobrir novos livros através da integração com a Google Books API. O aplicativo funciona completamente offline após a importação inicial dos dados.

**Aluno:** Enzo Heiden Januario  
**Curso:** Ciência da Computação  
**Disciplina:** Desenvolvimento de Aplicativos Móveis

## ✨ Funcionalidades Principais

- Cadastro manual e busca online de livros
- Acompanhamento de progresso de leitura (páginas lidas)
- Sistema de avaliação (1-5 estrelas) e notas pessoais
- Filtros por status (Quero Ler, Lendo, Lido) e categoria
- Estatísticas detalhadas (livros lidos, páginas totais, média de avaliações)
- Persistência local com Room Database

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Kotlin
- **Arquitetura:** MVVM (Model-View-ViewModel)
- **Banco de Dados:** Room Database
- **Networking:** Retrofit 2.9.0 + OkHttp 4.12.0
- **UI:** Material Design 3 com ViewBinding
- **Imagens:** Glide 4.16.0
- **Async:** Kotlin Coroutines + LiveData

## 📂 Estrutura do Projeto
```
com.example.booktracker/
├── data/
│   ├── local/              # Room Database (Entities, DAOs, Database)
│   ├── remote/             # Retrofit (API Service, Models)
│   └── repository/         # Repository Pattern
├── ui/
│   ├── main/               # Tela principal e lista de livros
│   ├── search/             # Busca via API
│   ├── details/            # Detalhes e edição
│   ├── add/                # Adicionar livro
│   ├── stats/              # Estatísticas
│   └── adapter/            # RecyclerView Adapters
└── utils/                  # Classes utilitárias
```

## 🗄️ Banco de Dados (Room)

### Entidades

**Book** - Informações do livro, status, progresso, avaliação e notas  
**Category** - Categorias de classificação (10 pré-cadastradas)  
**ReadingHistory** - Histórico temporal do progresso de leitura

### Relacionamentos
```
Category (1) ──> (N) Book
Book (1) ──> (N) ReadingHistory
```

### Operações CRUD

- **Create:** `insertBook()`, `insertCategory()`, `insertHistory()`
- **Read:** `getAllBooks()`, `getBookById()`, `getBooksByStatus()`, `getBooksByCategory()`
- **Update:** `updateBook()`
- **Delete:** `deleteBook()`

### Buscas Específicas
```kotlin
suspend fun getBookByIsbn(isbn: String): Book?
suspend fun getBookById(id: Int): Book?
fun getBooksByStatus(status: String): LiveData<List<Book>>
fun getBooksByCategory(categoryId: Int): LiveData<List<Book>>
```

## 🌐 Integração com API

### Google Books API

**Base URL:** `https://www.googleapis.com/books/v1/`

### Endpoints Utilizados

#### 1. Buscar Livros
```
GET /volumes?q={query}&maxResults={max}&langRestrict={lang}
```

**Parâmetros:**
- `q` (obrigatório): Termo de busca (título, autor ou ISBN)
- `maxResults` (opcional): Número máximo de resultados (padrão: 20)
- `langRestrict` (opcional): Idioma dos resultados (padrão: "pt")

**Exemplo:**
```
GET https://www.googleapis.com/books/v1/volumes?q=kotlin&maxResults=10&langRestrict=pt
```

**Resposta:**
```json
{
  "items": [
    {
      "id": "abc123",
      "volumeInfo": {
        "title": "Kotlin Programming",
        "authors": ["John Doe"],
        "pageCount": 450,
        "categories": ["Technology"],
        "imageLinks": {
          "thumbnail": "https://..."
        },
        "industryIdentifiers": [
          {
            "type": "ISBN_13",
            "identifier": "9781234567890"
          }
        ]
      }
    }
  ]
}
```

#### 2. Obter Detalhes do Livro
```
GET /volumes/{volumeId}
```

**Parâmetros:**
- `volumeId` (obrigatório): ID único do livro na API

**Exemplo:**
```
GET https://www.googleapis.com/books/v1/volumes/abc123
```

**Resposta:** Retorna informações completas do livro (mesma estrutura do objeto "item" acima)

### Sincronização

- **Direção:** One-way (API → Room Database)
- **Fluxo:** Usuário busca → API retorna dados → Dados salvos localmente → App funciona offline
- **Campo de referência:** `apiId` na entidade Book mantém vínculo com o livro original

## 🚀 Como Executar

### Pré-requisitos

- Android Studio Arctic Fox ou superior
- JDK 8 ou superior
- Android SDK (API 24 ou superior)
- Dispositivo Android ou Emulador

### Instruções

1. **Clone o repositório**
```bash
git clone https://github.com/seu-usuario/book-tracker-android.git
```

2. **Abra o projeto no Android Studio**
```
File > Open > Selecione a pasta do projeto
```

3. **Aguarde o Gradle Sync**
   - O Android Studio baixará automaticamente todas as dependências

4. **Execute o aplicativo**
   - Conecte um dispositivo Android via USB (com Depuração USB ativada)
   - OU inicie um emulador Android
   - Clique no botão **Run (▶️)** ou pressione **Shift + F10**

5. **Teste as funcionalidades**
   - Adicione livros manualmente ou busque online
   - Acompanhe o progresso de leitura
   - Visualize estatísticas

### Configuração Opcional

Se desejar usar outra API de livros, edite o arquivo:
```kotlin
data/remote/api/RetrofitClient.kt
```

## 📱 Requisitos do Sistema

- **Min SDK:** API 24 (Android 7.0)
- **Target SDK:** API 34 (Android 14)
- **Permissões necessárias:**
  - `INTERNET` - Para busca online de livros
  - `ACCESS_NETWORK_STATE` - Para verificar conectividade

## 📊 Diagrama de Navegação
```
MainActivity (Splash Screen)
    ↓
HomeFragment ──→ BookDetailsActivity (editar/excluir)
    ↓
    ├─→ SearchFragment ──→ AddBookActivity (adicionar da API)
    ├─→ StatsFragment
    └─→ AddBookActivity (cadastro manual)
```

## 📄 Licença

Projeto desenvolvido para fins educacionais como parte do curso de Ciência da Computação.

---

**Desenvolvido com Kotlin
