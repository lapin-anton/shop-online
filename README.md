# Shop-online

### ▎Витрина интернет-магазина

▎Описание приложения

> Витрина интернет-магазина — это веб-приложение, разработанное с использованием Spring Boot + WebFlux, которое предоставляет 
> пользователям возможность просматривать товары, добавлять их в корзину и оформлять заказы. 
> Приложение включает в себя удобный интерфейс для взаимодействия с товарами и корзиной покупателя, 
> а также возможности для управления заказами.

▎Функции приложения

• Страница витрины товаров: отображает список доступных товаров с информацией о названии, цене и изображении. Пользователи могут добавлять товары в корзину или удалять их.

• Пагинация: позволяет пользователям просматривать товары по страницам (10, 20, 50, 100 товаров).

• Фильтрация: пользователи могут фильтровать товары по названию, цене и алфавиту.

• Страница товара: предоставляет подробную информацию о товаре, включая описание и возможность изменения количества в корзине.

• Корзина покупателя: отображает все добавленные товары, их количество и общую стоимость. Предоставляет возможность изменения количества и удаления товаров.

• Страница заказов: показывает список всех оформленных заказов с возможностью просмотра деталей каждого заказа.

• Эмуляция оформления заказа: при нажатии на кнопку оформления заказа происходит переход на страницу с информацией о совершенном заказе.

• Загрузка товаров: возможность загрузки списка товаров на витрину через веб-форму.

▎Стек технологий

• Java 21

• Spring Boot 3.4.4

• Spring Web Flux

• Spring Data R2DBC

• Maven

• PostgreSQL

• JUnit 5 и Spring Boot Test, TestContainers, WebTestClient

• Docker

▎Инструкция по сборке и развертыванию

▎Предварительные требования

Перед началом работы убедитесь, что у вас установлены:

>• Java Development Kit (JDK) версии 21.
>
>• Сборщик проектов Maven.
>
>• PostgreSQL.
>
>• Docker.

▎Клонирование репозитория

Склонируйте проект из GitHub:

`git clone https://github.com/lapin-anton/shop-online.git`

`cd shop-online`

▎Настройка базы данных:

1. При подготовке приложения к сборке (используется PostgreSQL):

   • Создайте базу данных shop_online. файл с sql-скриптом лежит в директории `src/main/resources/sql/init_db.sql`

   • Настройте подключение в файле application.properties:

   `spring.datasource.url=jdbc:postgresql://host.docker.internal:5432/shop_online`

   `spring.datasource.username=<ваш-логин>`

   `spring.datasource.password=<ваш-пароль>`

2. При подготовке приложения к тестированию (используется TestContainers):

   • Никакой дополнительной настройки не требуется. База данных создастся автоматически в Docker при запуске 
   интеграционных тестов из sql-скрипта, размещенного в директории `src/test/resources/schema.sql`. 


▎Инструкция по развертыванию приложения в Docker

1. Сборка проекта:

   • Перейдите в корневую директорию вашего проекта и выполните команду сборки:

      `mvn clean package`


2. Создание Docker образа:

• Постройте Docker образ, выполнив команду в терминале, открытом в корневой папке проекта:

   `docker build -t shop-online .`


3. Запуск контейнера:

• Запустите контейнер, выполнив следующую команду:

   `docker run -d -p 8080:8080 --name shop-online shop-online`

4. Доступ к приложению:

   • Откройте браузер и перейдите по адресу http://localhost:8080, чтобы получить доступ к витрине интернет-магазина.

