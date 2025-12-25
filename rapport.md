## DAA lab-06
*Zaid Schouwey et Léonard Jouve*

Pour notre implémentation nous avons utilisé la démarche sans Jetpack Compose.

Nous avons créé un fragment `ContactFromFragment` qui sert à créer ou modifier un contact existant.\
Nous avons utilisé une architecture MVVM afin de respecter le principe de single responsibility.\
Le fragment expose une méthode static `newInstance` permettant de l'instancier avec en argument l'id du contact à modifier ou `null` pour créer un nouveau contact.\
Le view model est instancié comme vu lors des laboratoires précédents.\
Une fois le fragment créé, le contact à modifier est récupéré de la base de donnée, si il y en a un. Sinon, la live data `contact` stock la valeur `null`.

Le view model expose toutes les méthodes nécéssaires à stocker les contacts dans la base de données et à la communication avec l'API.

Selon l'algorithme vu en cours, lors de la modification d'une ressource, elle est premièrement effectuée sur la base de données locale avec un status indiquant la modification. Une tentative de synchronisation avec le serveur est ensuite effectuée et finalement la base de données est mise à jour pour indiquer que la ressource est synchronisée (status = SyncStatus.OK).\
Dans le cas ou celà échoue, le bouton update permet de faire une nouvelle tentative de synchronisation au serveur.

Nous avons créé une nouvelle entité `SyncContact` dans la base de données contenant un id, un status de synchronisation et un contact.

Nous avons utilisé la librairie ktor comme client HTTP.
Afin de pouvoir envoyer un contact au format json à l'API, nous avons du créer un serializer `CalendarSerializer` pour l'attribut `birthday` et marquer l'entité `@Serializable`.

La méthode `enroll` permet de supprimer tous les contacts existants dans la base de données, de demander à l'API une nouvelle session et de réccupérer ses contacts.

La méthode `create` crée un nouveau contact, lui ajoute l'état CREATED dans la base de données et tente de le synchroniser avec le serveur.

La méthode `update` met à jour un contact existant, lui ajoute l'état UPDATED dans la base de données et tente de le synchroniser avec le serveur.
Nous avons pris la décision de garder l'état CREATED si le contact n'avais toujours pas pu être synchronisé avec le serveur afin de permettre la modification de contact non-synchronisé.

La méthode `delete` ajoute l'état DELETED à un contact existant dans la base de données et tente de le synchroniser avec le serveur.
Nous avons pris la décision de supprimer complètement le contact de la base de données si il n'avais pas encore été créé sur le serveur afin de permettre la suppression de contact non-synchronisé.

La méthode `refresh` tente de synchroniser tous les contacts qui ne sont pas dans l'état OK avec le serveur.

Afin de stocker l'id de session de manière persistante, nous avons créé une class `SessionManager` utilisée par le view model et instanciée dans la class `ContactsApplication`. Elle utilise les shared preferences de l'application pour le stockage.

Les fonctions du view model effectuent toutes les opérations bloquantes dans des coroutines afin de ne pas bloquer le fil d'execution.