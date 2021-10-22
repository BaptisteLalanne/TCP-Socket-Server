
**Auteur: Tom Perrillat-Collomb Baptiste Lalanne**
**Date: 22/10**

## 1. Description

Second projet réalisé en binôme pour le module PR: Programmation Réseaux.
L'objectif est de mettre en place un serveur web utilisant les sockets pour les communications entre serveur et client(s).


## 2. Exécution

#### 2.1. Compilation

Un Makefile (dans le dossier `src`) permet de compiler l'ensemble du projet. Le Makefile propose deux commandes :

- `make clean`: supprime les fichiers compilés `.class`,
- `make` : compile les fichiers `.java` en `.class`.

#### 2.2. Lancement du serveur

Un script `server.sh` compile le projet (commandes `make clean` et `make`) puis exécute le serveur.
Depuis le dossier `src`, exécuter :

```bash
./run_server
```

Le serveur web est ensuite accessible depuis un navigateur à l'adresse `localhost:8000`.

#### 2.3. Lancement des scripts clients

Pour tester certaines méthodes (`PUT`, `DELETE`), des scripts ont été réalisés. Depuis le dossier `src`, exécuter :

```bash
./put.sh
./delete.sh
./post.sh
```

## 3. Fonctionnalités

Depuis un navigateur web, l'adresse `localhost:8000` affiche une page listant l'ensemble des fonctionnalités offertes par le serveur. Celles-ci sont :
- [GET] affichage d'une page HTML,
- [GET] affichage d'une image,
- [GET] affichage d'une vidéo,
- [GET] affichage d'une page dynamique javascript,
- [POST] envoi d'un formulaire.

Les scripts bash disponibles sont :
- [PUT] `./put.sh` : envoi des données qui sont sauvegardés dans un fichier `put.txt`, accessible depuis un navigateur à l'adresse `localhost:8000/put.txt`,
- [DELETE] `./delete.sh` : fonctionnalité non développée, utilisée pour tester l'erreur 501,
- [POST] `./post.sh` : envoi des données qui sont renvoyées au client dans un fichier HTML.
