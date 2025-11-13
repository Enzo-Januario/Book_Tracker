# 📚 Book Tracker

Aplicativo Android para gerenciar sua biblioteca pessoal e acompanhar progresso de leitura.

## ✨ Funcionalidades

- 📖 Cadastro manual e busca online de livros (Google Books API)
- 📊 Acompanhamento de progresso de leitura
- ⭐ Sistema de avaliação e notas pessoais
- 🔍 Filtros por status e categoria
- 📈 Estatísticas de leitura
- 🎨 Interface moderna Material Design 3

## 🛠️ Tecnologias

- **Linguagem:** Kotlin
- **Arquitetura:** MVVM
- **Banco de Dados:** Room
- **Networking:** Retrofit + Google Books API
- **UI:** Material Design 3 + ViewBinding
- **Imagens:** Glide

## 📂 Estrutura
```
├── data/local/          # Room Database (Entities, DAOs)
├── data/remote/         # Retrofit API
├── data/repository/     # Repository Pattern
├── ui/                  # Activities & Fragments
└── utils/               # Utilitários
```

## 🗄️ Banco de Dados

**3 Entidades:**
- Book (Livro)
- Category (Categoria)
- ReadingHistory (Histórico)

**Relacionamentos:**
- Category 1:N Book
- Book 1:N ReadingHistory

## 🌐 API

**Google Books API**
- Busca de livros online
- Importação de dados para uso offline

## 🚀 Como Executar
```bash
# 1. Clone o repositório
git clone https://github.com/SEU_USUARIO/book-tracker-android.git

# 2. Abra no Android Studio

# 3. Execute o app (▶️)
```

**Requisitos:**
- Android Studio
- Min SDK: 24 (Android 7.0)

## 👨‍💻 Autor

**Enzo Heiden Januario**
- Ciência da Computação
- Projeto Final - Desenvolvimento de Aplicativos Móveis

## 📄 Licença

Projeto desenvolvido para fins educacionais.

---

Desenvolvido com Kotlin
