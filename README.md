# DataVault

## Description du projet

DataVault est une application Android developpee en Java illustrant les differentes methodes de persistance locale des donnees sur un appareil Android. Elle couvre les preferences partagees non chiffrees, le stockage chiffre de secrets via EncryptedSharedPreferences, les fichiers internes au format texte et JSON, le cache temporaire et le stockage externe app-specific. L'application fonctionne entierement hors connexion et applique des regles strictes de securite pour la gestion des donnees sensibles.

---

## Structure du projet

Le projet est organise en packages distincts separant chaque responsabilite :

- `ui` : contient `MainActivity` qui orchestre toutes les interactions utilisateur
- `prefs` : contient `UserPrefs` pour les preferences non sensibles et `VaultPrefs` pour le stockage chiffre
- `files` : contient `TextFileStore` pour les fichiers texte et `NotesJsonStore` pour la serialisation JSON
- `cache` : contient `TempStore` pour le stockage temporaire dans le repertoire cache
- `external` : contient `ExternalStore` pour l'export vers le stockage externe app-specific
- `model` : contient la classe `Note` representant le modele de donnees

---

## Modele de donnees

La classe `Note` represente l'objet metier de l'application. Elle contient trois champs publics finaux : un identifiant entier, un titre et un contenu textuel. Ce modele est utilise pour la serialisation JSON et l'affichage dans l'interface.

---

## Fonctionnalites

**Preferences utilisateur**

La classe `UserPrefs` gere les preferences non sensibles via `SharedPreferences` en mode `MODE_PRIVATE`. Elle stocke le nom d'utilisateur, la langue choisie parmi quatre options et le theme de l'interface. La methode `save()` accepte un parametre booleen permettant de choisir entre `apply()` pour une ecriture asynchrone et `commit()` pour une ecriture synchrone avec confirmation. La methode `load()` retourne un objet `Snapshot` encapsulant les trois valeurs. La methode `clear()` efface toutes les preferences.

**Stockage chiffre du token**

La classe `VaultPrefs` utilise `EncryptedSharedPreferences` avec une `MasterKey` basee sur le schema AES256_GCM pour chiffrer a la fois les cles et les valeurs sur le disque. Le token est stocke via `storeToken()` et recupere via `fetchToken()`. Le token n'est jamais affiche en clair ni consigne dans les logs — seule sa longueur est affichee pour verification. En cas d'erreur de chiffrement, une exception est capturee et un message generique est affiche sans fuite d'information.

**Fichiers internes texte**

La classe `TextFileStore` permet d'ecrire et de lire des fichiers texte en encodage UTF-8 dans le stockage interne prive de l'application via `openFileOutput()` et `openFileInput()`. Elle utilise des flux classiques `FileOutputStream` et `FileInputStream` compatibles avec toutes les versions Android depuis API 24. La methode `delete()` supprime le fichier via `deleteFile()`.

**Fichiers internes JSON**

La classe `NotesJsonStore` serialise et deserialise une liste d'objets `Note` au format JSON en utilisant `JSONArray` et `JSONObject` de la bibliotheque standard Android. En cas de fichier absent ou corrompu, la methode `load()` retourne une liste vide sans provoquer de crash. Le fichier est stocke sous le nom `notes.json` dans le repertoire interne prive.

**Cache temporaire**

La classe `TempStore` ecrit et lit des fichiers dans le repertoire `cacheDir` de l'application, reserve aux donnees temporaires regenerables. La methode `purge()` supprime tous les fichiers du cache et retourne le nombre de fichiers supprimes. Le cache est utilise pour sauvegarder un resume de session apres chaque sauvegarde de preferences.

**Stockage externe app-specific**

La classe `ExternalStore` ecrit des fichiers dans le repertoire externe prive de l'application via `getExternalFilesDir(null)`, sans necessiter de permission `WRITE_EXTERNAL_STORAGE`. La methode `write()` retourne le chemin absolu du fichier cree pour verification. L'export genere un fichier `export_notes.txt` contenant toutes les notes avec un horodatage.

**Interface utilisateur**

L'interface est organisee en cartes `MaterialCardView` avec un theme bleu fonce. La premiere carte regroupe les preferences utilisateur avec un champ texte pour le nom, un spinner pour la langue, un switch pour le mode sombre et un champ mot de passe pour le token chiffre. La deuxieme carte regroupe les operations sur les notes. Un bouton rouge de reinitialisation complete efface toutes les donnees. Une carte de sortie affiche le resultat de chaque operation en temps reel.

**Nettoyage complet**

Le bouton Clear Everything execute un nettoyage complet en une seule action : effacement des preferences utilisateur, effacement des preferences chiffrees, suppression de `notes.json`, suppression de `memo.txt`, suppression du fichier export externe et purge du cache. Le nombre de fichiers de cache supprimes est affiche dans le resultat.

---

## Regles de securite appliquees

- Les preferences non sensibles sont stockees avec `MODE_PRIVATE` uniquement
- Le token est stocke exclusivement via `EncryptedSharedPreferences` avec AES256_GCM
- Le token n'apparait jamais dans les logs ni dans l'interface en clair
- Le champ token utilise `inputType="textPassword"` pour masquer la saisie
- Toutes les exceptions sont capturees sans exposer d'informations sensibles
- Le cache est reserve aux donnees temporaires regenerables
- L'export externe est limite au repertoire app-specific non public
- L'encodage UTF-8 est impose pour tous les fichiers texte
- La longueur du token est la seule information affichee pour confirmer sa presence

---

## Differences par rapport au sujet original

- Modele de donnees `Note` avec `title` et `content` au lieu de `Student` avec `name` et `age`
- Classe `UserPrefs` avec objet `Snapshot` au lieu de `AppPrefs` avec objet `Triple`
- Classe `VaultPrefs` avec methodes `storeToken` et `fetchToken` au lieu de `SecurePrefs` avec `saveToken` et `loadToken`
- Classe `NotesJsonStore` au lieu de `StudentsJsonStore` avec donnees differentes
- Classe `TextFileStore` au lieu de `InternalTextStore`
- Classe `TempStore` au lieu de `CacheStore`
- Classe `ExternalStore` au lieu de `ExternalAppFilesStore`
- Interface entierement en anglais avec theme bleu fonce et cartes MaterialCardView
- Quatre langues dans le spinner au lieu de trois
- Bouton d'export comme fonctionnalite separee generant un rapport complet avec horodatage
- Carte de sortie dediee au lieu d'un TextView simple
- Toutes les operations encapsulees dans des blocs try-catch pour eviter les crashes
- Remplacement de `Files.writeString()` par des flux IO classiques pour une compatibilite maximale
- Remplacement de `isBlank()` par `isEmpty()` pour la compatibilite API 24

---

## Installation

1. Ouvrir le projet dans Android Studio
2. Laisser Gradle synchroniser la dependance `androidx.security:security-crypto`
3. Lancer l'application sur un emulateur ou un appareil physique avec API 24 minimum
4. Aucune connexion Internet requise

---

## Technologies utilisees

- Langage : Java
- Environnement : Android Studio
- SDK minimum : API 24 (Android 7.0)
- Composants principaux : SharedPreferences, EncryptedSharedPreferences, MasterKey, FileOutputStream, FileInputStream, JSONArray, JSONObject, MaterialCardView, Spinner, Switch

  
## Video:
https://github.com/user-attachments/assets/8f7f6106-95cc-4b76-8988-eafeddb72d0b

