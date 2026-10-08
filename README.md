# Заметки

![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?logo=kotlin&logoColor=white)
![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-UI-4285F4?logo=jetpackcompose&logoColor=white)
![Hilt](https://img.shields.io/badge/Hilt-2.60.1-29B6F6)
![Room](https://img.shields.io/badge/Room-2.8.4-4CAF50?logo=sqlite&logoColor=white)
![Coil](https://img.shields.io/badge/Coil-3.6.3-FF7043)
![Min SDK](https://img.shields.io/badge/Min%20SDK-24-orange)

Современное оффлайн-приложение для ведения заметок и задач с поддержкой быстрого поиска, локального хранения данных, RU и ENG языков. 

Разработано на **Kotlin** и **Jetpack Compose** с применением принципов **Clean Architecture** и паттерна **MVVM**. Для управления состоянием и асинхронностью используются **Coroutines** и **StateFlow**, внедрение зависимостей реализовано через **Hilt**.

## Скриншоты
| Главный экран | Создание заметки | Редактирование заметки |
| :---: | :---: | :---: |
| <img width="356" height="747" alt="image" src="https://github.com/user-attachments/assets/d65bde26-2b38-41ce-a398-f0b8ac35279d" /> | <img width="360" height="750" alt="image" src="https://github.com/user-attachments/assets/df0af9fe-fe32-4976-9944-41816139de73" /> | <img width="357" height="748" alt="image" src="https://github.com/user-attachments/assets/e2e53bae-d948-403b-b554-ad950e42cb0c" />

## Основные возможности

| Возможность | Описание |
| :--- | :--- |
| **Создание, редактирование заметок** | Простой способ создать новую или организовать старую заметку. Возможность удаления заметки |
| **Offline-first** | Полная работоспособность без интернета, данные хранятся на устройстве |
| **Современный UI/UX** | Плавные переходы, Material 3 дизайн |
| **Эффективная загрузка изображений** | Асинхронный рендеринг картинок и иконок |

## Технологический стек

- **Язык:** Kotlin
- **UI:** Jetpack Compose, Material 3, Activity Compose, Lifecycle ViewModel Compose
- **Архитектура:** Clean Architecture, MVVM
- **Внедрение зависимостей (DI):** Hilt
- **Локальное хранение:** Room Database
- **Сериализация:** Kotlinx Serialization JSON
- **Асинхронная загрузка изображений:** Coil

**UI-концепт приложения взят из открытого макета в Figma:** 

- Notes Taking App: https://www.figma.com/community/file/1290028598284961246/notes-taking-app

- Автор: https://www.figma.com/@b1a21c38_3e2c_4

## Архитектура проекта

Проект структурирован по принципам **Clean Architecture** с разделением на независимые слои:

- `com/example/notes/domain` — Чистая бизнес-логика (модели сущностей, Use Cases, интерфейсы репозиториев). Не имеет внешних зависимостей.
- `com/example/notes/data` — Реализация репозиториев, работа с источниками данных (Room DAO, маппинг сущностей в доменные модели).
- `com/example/notes/presentation` — Экраны, `ViewModel`, управление UI-состоянием и событиями.

## Сборка и запуск
1. Откройте Android Studio.
2. Клонируйте репозиторий:
```bash
git clone https://github.com/ermakown/Notes.git
```
4. Запустите приложение на эмуляторе или физическом устройстве с Android 7.0 (API 24) и выше.
