import java.util.*;
import java.io.*;


public class Login implements Serializable {
    protected String login;
    protected String password;
    private Etudiant etudiant;

    public Login() {
        etudiant = null;
    }

    public String getLogin() {
        return this.login;
    }

    public Etudiant getEtudiant() {
        return this.etudiant;
    }

    class WrongPwdException extends Exception{

        public String toString(){
            return "Erreur: Mauvais mot de passe";
        }
    }

    class WrongInputLengthException extends Exception{

        public String toString(){
            return "Erreur: Trop de caractères en entrée";
        }
    }

    class WrongLoginException extends Exception{

        public String toString(){
            return "Erreur: Pseudo inexistant";
        }
    }

    class PseudoIndisponibleException extends Exception{

        public String toString(){
            return "Erreur: Pseudo deja existant";
        }
    }

    class WrongInfoException extends Exception{

        public String toString(){
            return "Erreur: Nom ou prenom invalide";
        }
    }

    class AvatarExistantException extends Exception {
        public String toString() {
            return "Vous posséder déjà un avatar.";
        }
    }

    // Retourne l'etudiant connecter
    public Etudiant trouveEtudiantConnecter() {
        ArrayList<Etudiant> listeEtudiant = Etudiant.chargerEtudiant();
        for (Etudiant etudiant : listeEtudiant) {
            Avatar avatar = etudiant.getAvatar();
            if (avatar.getPseudo().equals(login)) {
                return etudiant;
            }
        }
        return null;
    }

    // Permet a l'etudiant de s'identifier lors de l'inscription
    public Etudiant getUserInfo() throws WrongInfoException, AvatarExistantException {

        Scanner sc = new Scanner(System.in) ;

        System.out.println("Quel est votre nom");
        String nom = sc.nextLine();

        System.out.println("Quel est votre prenom");
        String prenom = sc.nextLine();
        ArrayList<Etudiant> listeEtudiants = Etudiant.chargerEtudiant();
        for (Etudiant etudiant : listeEtudiants) {
            if (etudiant.getNom().equals(nom)) {
                if (etudiant.getPrenom().equals(prenom)) {
                    if (!(etudiant.getAvatar() == null)) {
                        throw new AvatarExistantException();
                    }
                    return etudiant;
                }            
            }
        }
        throw new WrongInfoException();
    }

    protected void getUserPwd() throws WrongLoginException, WrongPwdException, WrongInputLengthException {

        Scanner sc = new Scanner(System.in) ;

        System.out.println("Pseudo : ");
        login = sc.nextLine();

        System.out.println("Mot de passe:");
        password = sc.nextLine();

        Map<String, String> logins = chargerLogins();

        if (login.length() > 10 || password.length() > 10)
            throw new WrongInputLengthException();

        if (!logins.containsKey(login))
            throw new WrongLoginException();

        if (!logins.get(login).equals(password))
            throw new WrongPwdException();
    }


    public void connexion(){
        boolean valide = false;

        while( !valide ){
            try {
                getUserPwd();
                System.out.println("Login/password valides");
                etudiant = trouveEtudiantConnecter();
                valide = true;
            }
            catch(WrongLoginException wle){
                wle.printStackTrace();
            }

            catch(WrongPwdException wpe) {
                wpe.printStackTrace();
            }

            catch(WrongInputLengthException wile){
                wile.printStackTrace();
                System.exit(0); //risque de hacking, on sort du programme
            }
            catch(Exception e){
                e.printStackTrace();
                System.exit(0); //pb inconnu; on sort de la méthode pour limiter les risques
            }
        }
    }

    public void getUserPwdIncrip() throws WrongInputLengthException, PseudoIndisponibleException {

        Scanner sc = new Scanner(System.in) ;

        System.out.println("Pseudo : ");
        login = sc.nextLine();

        System.out.println("Mot de passe:");
        password = sc.nextLine();

        Map<String, String> logins = chargerLogins();


        if (logins.containsKey(login))
            throw new PseudoIndisponibleException();

        if (login.length() > 10 || password.length() > 10)
            throw new WrongInputLengthException();
    }


    public void creerCompte() {
        boolean valide = false;
        while( !valide ){
            try {
                getUserPwdIncrip();
                System.out.println("Compte créé!");
                        valide = true;
            }
            catch(PseudoIndisponibleException pie){
                pie.printStackTrace();
            }

            catch(WrongInputLengthException wile){
                wile.printStackTrace();
                System.exit(0); //risque de hacking, on sort du programme
            }
            catch(Exception e){
                e.printStackTrace();
                System.exit(0); //pb inconnu; on sort de la méthode pour limiter les risques
            }
        }
        Map<String, String> logins = chargerLogins();
        logins.put(login, password);
        sauvegarderLogins(logins);
    }

    public void inscription() {
        Boolean insciption = false;
        while (!insciption) {
            try {
                etudiant = getUserInfo();
                System.out.println("Bienvenue");
                creerCompte();
                String pseudo = getLogin();
                etudiant.creerAvatar(pseudo);
                insciption = true;
            }
            catch(WrongInfoException wie){
                wie.printStackTrace();
            }

            catch(AvatarExistantException aee){
                aee.printStackTrace();
                break;
            }
        }
    }

    // Écrit tous les etudiants dans un nouveau fichier
    protected static void sauvegarderLogins(Map<String, String> logins) {
        List<Map.Entry<String, String>> entries = new ArrayList<>(logins.entrySet());
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("Login.ser"))) {
            oos.writeObject(entries);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    
    // Charge tous les logins depuis le fichier
    protected static Map<String, String> chargerLogins() {
        Map<String, String> map = new HashMap<>();
        File file = new File("Login.ser");
        if (file.exists()) {
            try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
                Object obj = ois.readObject();
                if (obj instanceof List<?>) {
                    List<Map.Entry<String, String>> entries = (List<Map.Entry<String, String>>) obj;
                    for (Map.Entry<String, String> entry : entries) {
                        map.put(entry.getKey(), entry.getValue());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return map;
    }
    

}