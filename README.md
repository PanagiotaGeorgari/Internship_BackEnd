# Test Cases

---

## CardServiceImpl

| Method         | Test Case                                                                                         | Result                                                                                       |
|----------------|-------------------------------------------------------------------------------------------------|----------------------------------------------------------------------------------------------|
| **getById**    | Το cardId ανήκει στο currentUser και ανήκει στο repo                                             | Επιστρέφει `GetByIdResponse` με τις κάρτες που έχει ήδη Assocs                              |
|                | Το cardId δεν ανήκει στο repo                                                                   | `Throw new CardNotFoundException()`                                                        |
|                | Το cardId υπάρχει στο repo, ο current user είναι Member και δεν έχω πρόσβαση                     | `Throw new AccessDeniedException()`                                                        |
| **deleteCard** | Το cardId ανήκει στο currentUser (Admin, Member) και ανήκει στο repo                             | Διαγράφει την κάρτα από το repo και επιστρέφει void                                        |
|                | Το cardId δεν ανήκει στο repo                                                                   | `Throw new CardNotFoundException()`                                                        |
|                | Το cardId υπάρχει στο repo, ο current user είναι Member και δεν έχω πρόσβαση                     | `Throw new AccessDeniedException()`                                                        |
| **replaceCard**| Το cardId υπάρχει στο repo και είναι Admin                                                      | Αποθηκεύει στο repo την ανανεωμένη κάρτα και την επιστρέφει                                 |
|                | Το cardId δεν ανήκει στο repo                                                                   | `Throw new CardNotFoundException()`                                                        |
|                | Το cardId υπάρχει στο repo, ο current user είναι Member και δεν έχω πρόσβαση                     | `Throw new AccessDeniedException()`                                                        |
|                | Το cardId υπάρχει στο repo και είναι Member που δικαιούται να έχει πρόσβαση                      | Αποθηκεύει στο repo την ανανεωμένη κάρτα και την επιστρέφει                                 |
| **partialUpdateCard** | Το cardId υπάρχει στο repo και είναι Admin                                                | Ανανεώνει την κάρτα και την επιστρέφει                                                      |
|                | Το cardId δεν ανήκει στο repo                                                                   | `Throw new CardNotFoundException()`                                                        |
|                | Το cardId υπάρχει στο repo, ο current user είναι Member και δεν έχω πρόσβαση                     | `Throw new AccessDeniedException()`                                                        |
|                | Το cardId υπάρχει στο repo και είναι Member που δικαιούται να έχει πρόσβαση                      | Ανανεώνει την κάρτα και την επιστρέφει                                                      |
| **newCard**    | Παίρνει ως είσοδο μια κάρτα                                                                     | Αποθηκεύει την κάρτα στο repo και την επιστρέφει                                           |
| **getCardsPagination** | Αν ο currentUser είναι Admin                                                               | Παίρνει από το repo όλες τις κάρτες και τις επιστρέφει ως αντικείμενο pageable             |
|                | Αν ο currentUser είναι Member                                                                    | Παίρνει από το repo μόνο τις δικές του κάρτες και τις επιστρέφει ως αντικείμενο pageable  |
| **getCurrentUser** | Όταν ο χρήστης είναι κανονικά συνδεδεμένος                                                  | Επιστρέφει το user object                                                                 |
|                | Όταν δεν έχει συμπληρωθεί το authentication field                                               | `Throw AnonymousAuthenticationToken()`                                                    |

---

## UserServiceImpl

| Method           | Test Case                                                                                     | Result                                                                                   |
|------------------|-----------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| **createUser**   | Προσπαθούμε να αποθηκεύσουμε ένα νέο User στο repository                                      | Το νέο αντικείμενο User που αποθηκεύτηκε στο repository                                 |
| **validateUser** | Το email input υπάρχει στο repository                                                         | Κωδικοποιεί το password και επιστρέφει true αν το encoded ταιριάζει με το input         |
|                  | Το email input υπάρχει στο repository                                                         | Κωδικοποιεί το password και επιστρέφει false αν το encoded δεν ταιριάζει με το input    |
|                  | Το email input δεν υπάρχει στο repository                                                     | `Throw UsernameNotFoundException()`                                                     |
| **loadUserByUsername** | Το email input υπάρχει στο repository                                                     | Επιστρέφει ένα αντικείμενο UserDetails                                                 |
|                  | Το email input δεν υπάρχει στο repository                                                     | `Throw UsernameNotFoundException()`                                                     |

---

## AssocServiceImpl

| Method           | Test Case                                                                                     | Result                                                                                   |
|------------------|-----------------------------------------------------------------------------------------------|------------------------------------------------------------------------------------------|
| **newAssoc**     | Η δεξιά κάρτα του assoc input δεν υπάρχει στο repository                                     | `Throw CardNotFoundException()`                                                         |
|                  | Η αριστερή κάρτα του assoc input δεν υπάρχει στο repository                                  | `Throw CardNotFoundException()`                                                         |
|                  | Αν οι κάρτες υπάρχουν στο repository και ο current user δεν έχει δικαίωμα πάνω στις κάρτες (!validateOwner) | `Throw AccessDeniedException()`                                                  |
|                  | Αν οι κάρτες υπάρχουν στο repository, ο current user έχει δικαίωμα πάνω στις κάρτες (validateOwner) και οι κάρτες αυτές ήδη συσχετίζονται (!uniqueAssoc) | `Throw AssocAlreadyExistsException()`                                   |
|                  | Αν οι κάρτες υπάρχουν στο repository, ο current user έχει δικαίωμα πάνω στις κάρτες (validateOwner) και οι κάρτες αυτές δεν συσχετίζονται ήδη (uniqueAssoc) | Αποθηκεύω στο repository την assoc και την inversed assoc και επιστρέφω λίστα με τα νέα assoc_ids |
| **validateOwner**| Αν ο current user είναι Admin                                                                | true                                                                                     |
|                  | Αν ο current user είναι Member και οι δημιουργοί τους είναι διαφορετικοί                       | false                                                                                    |
|                  | Αν ο current user είναι Member και οι δημιουργοί τους είναι ο ίδιος user                      | true                                                                                     |
| **uniqueAssoc**  | Αν υπάρχει στο assoc input στο repo ή υπάρχει το αντίστροφο (κυκλική)                        | false                                                                                    |
|                  | Αν δεν υπάρχει στο assoc input στο repo ούτε υπάρχει το αντίστροφο (κυκλική)                 | true                                                                                     |
| **createAssoc**  | Δέχεται τα στοιχεία του Assoc                                                                | Επιστρέφει το νέο Assoc object                                                          |
| **deleteAssoc**  | Το assoc id input δεν υπάρχει στο repository                                                 | `Throw AssocNotFoundException()`                                                        |
|                  | Αν ο current user είναι Admin                                                                | Διαγράφει από το repository το assoc και το inv_assoc (void)                            |
|                  | Αν ο current user είναι Member και του ανήκουν και οι δύο κάρτες                              | Διαγράφει από το repository το assoc και το inv_assoc (void)                            |
|                  | Αν ο current user είναι Member και δεν του ανήκουν και οι δύο κάρτες                         | `Throw AccessDeniedException()`                                                         |
| **getCardAssocs**| Δέχεται ένα cardId                                                                           | Επιστρέφει λίστα με τα Assoc DTO που συμμετέχει το cardId                               |
| **getCardAssocsByType** | Δέχεται ένα cardId                                                                     | Επιστρέφει λίστα με τα Assoc που συμμετέχει το cardId με το συγκεκριμένο assocType     |
| **getAssoc**     | Δέχεται τα card_ids του assoc και αν υπάρχει στο repository assoc που τις συσχετίζει       | Επιστρέφει το assoc που συμμετέχουν και οι δύο κάρτες                                 |
|                  | Δέχεται τα card_ids του assoc και αν δεν υπάρχει στο repository assoc που τις συσχετίζει   | `Throw AssocNotFoundException()`                                                       |
| **getCurrentUser** | Ο χρήστης είναι συνδεδεμένος                                                              | Επιστρέφει το αντικείμενο User                                                         |
|                    | Ένας χρήστης δεν έχει κάνει login, το authentication συχνά δεν είναι null                   | `Throw ClassCastException()`                                                           |
