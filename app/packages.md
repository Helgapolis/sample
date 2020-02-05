# Package com.kastapp.sample.ui

Слой презентации. Разбиение по пакетам фич. В пакете фрагменты, активности, адаптеры, ViewModel и тд.

# Package com.kastapp.sample.data.model

Классы данных которые могут использоваться на всех слоях. 
Модель может быть Dto и Entity одновременно в случае если поля требующиеся для DB и UI полностью совпадают. 
В противном случае происходит маппинг через котлиновские расширения в Mapper.kt, а Dto классы распологаются в пакетах com.kastapp.sample.data.remote.response и com.kastapp.sample.data.remote.request, а Entity классы в пакете com.kastapp.sample.data.local.db.entity

# Package com.kastapp.sample.data.local.db

Все что касается локальной базы данных.

# Package com.kastapp.sample.data.local.db.entity

Сущности которые использутся для создания таблицы в бд. 
Здесь следует создавать класс только в том случае если он будет использоваться только на уровне репозитория/интерактора. 
Если сущность будет использоваться на всех слоях, то ее стоит поместить в пакет com.kastapp.sample.data.model

# Package com.kastapp.sample.data.local.db.dao

DAO классы, содержащие CRUD методы для конкретной сущности.

# Package com.kastapp.sample.data.local

Локальное хранилище данных. БД, SharedPreferences.

# Package com.kastapp.sample.data.local.prefs

Все что касается SharedPreferences

# Package com.kastapp.sample.data.remote

Все что касается работы с сетью.

# Package com.kastapp.sample.data.remote.request

Dto классы. Служат для передачи данных от клиента к серверу. Экземпляр Dto класса может быть инициализирован на уровне слоя презентации либо на уровне слоя данных.

# Package com.kastapp.sample.data.remote.response

Dto классы. Служат для получения данных от сервера. Может быть как оберткой над моделью так и полноценным Dto в случае маппинга в модель.

# Package com.kastapp.sample.di

Построение дерева зависимостей. Буква D из принципа SOLID (тобишь зависимость класса от интерфейса) применяется если нам действительно это будет нужно, например для написания фейковых классов для тестов, в противном случае руководствуемся принципом YAGNI.

# Package com.kastapp.sample.interactor
Классы необходимые для взаимодействия между несколькими репозиториями, может содержать другой интерактор.
Также в интерактор может отправляться событие(например пришел пуш). На это событие могут быть подписаны другие экраны во ViewModel. 
Является синглитоном.

# Package com.kastapp.sample.data.repository

Классы отвечающие за работу с данными. Их получение, хранение и маппинг. Является синглитоном.

# Package com.kastapp.sample.util

Общие классы утилиты и расширения для других библиотек.

# Package com.kastapp.sample.ui.common.adapter

Общие адаптеры.

# Package com.kastapp.sample.ui.common.controller

Общие вспомогательные классы для активности и фрагмента.

# Package com.kastapp.sample.ui.common.dialog

Общие диалоговые окна.

# Package com.kastapp.sample.ui.common.ext

Расширения используемые на ui.

# Package com.kastapp.sample.ui.common.view

Кастомные вьюхи.






