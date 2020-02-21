package de.mid.consulting.innovator.base;

import de.mid.consulting.innovator.base.exception.InnovatorConnectionException;
import de.mid.consulting.innovator.base.exception.codes.FehlercodesInnovatorConnection;
import de.mid.consulting.innovator.base.output.Output;
import de.mid.consulting.innovator.base.output.messages.LogMessageBuilder;
import de.mid.consulting.innovator.element.wrapper.ModelW;

import de.mid.innovator.net.InoNetException;
import de.mid.innovator.srv.LicenseServer;
import de.mid.innovator.srv.Model;
import de.mid.innovator.srv.RepositoryServer;
import de.mid.innovator.srv.SrvErrorException;

import java.util.ArrayList;
import java.util.List;

/**
 * Führt Anwendungen vom Typ InnoBaseApp aus.
 * @author sauerbies
 */
public class InnoAppRunner {
    private String hostname;
    private int port;
    private String repositoryServerName;
    private String modelName;
    private String user;
    private String password;
    private String rolle;

    /**
     * Initialisiert den App-Runner mit den Daten der Verbindung zum Innovator.
     * @param hostname
     * @param port
     * @param repositoryServerName
     * @param modelName
     * @param user
     * @param password
     * @param rolle
     */
    public InnoAppRunner(String hostname, int port, String repositoryServerName, String modelName, String user, String password, String rolle) {
        super();
        this.hostname = hostname;
        this.port = port;
        this.repositoryServerName = repositoryServerName;
        this.modelName = modelName;
        this.user = user;
        this.password = password;
        this.rolle = rolle;
    }

    /**
     * Initialisiert den App-Runner mit den Daten der Verbindung zum Innovator.
     * @param hostname
     * @param port
     * @param repositoryServerName
     * @param modelName
     */
    public InnoAppRunner(String hostname, int port, String repositoryServerName, String modelName) {
        this(hostname, port, repositoryServerName, modelName, null, null, null);
    }

    /**
     * Führt eine InnoApp aus.
     * @param application
     */
    public void runApp(InnoBaseApp application) {
        List<InnoBaseApp> applicationList = new ArrayList<InnoBaseApp>();
        applicationList.add(application);
        runApps(applicationList);
    }

    /**
     * Führt eine Menge von InnoApps in einer gemeinsamen Innovator-Sitzung aus.
     * @param applications
     */
    public void runApps(List<InnoBaseApp> applications) {
        try {
            LicenseServer licSrv = connectLicenceServer();

            boolean wasLoggedIn = true;// Merkt sich ob der Nutzer bereits angemeldet war.
            // (true, damit, falls es früher knallt keine Folgefehler entstehen.)

            RepositoryServer repositoryServer = getRepositoryServer(repositoryServerName, licSrv);
            try {
                repositoryServer.connect();
                Model model = findModel(modelName, repositoryServer);
                try {
                    wasLoggedIn = login(model);
                    InnoMetaInfo innoMetaInfo = InnoMetaInfo.getInstance();
                    innoMetaInfo.initModel(new ModelW(model));
                    for (InnoBaseApp app : applications) {
                        Output.info("Starte App: " + app.getClass().toString());
                        app.run();
                    }
                } finally {
                    Output.printAllMessages();// Leert die Messages
                    if (!wasLoggedIn) {// Trennt die Verbindung, wenn der Nutzer nicht angemeldet war
                        model.logout();
                    }
                }
            } finally {
                if (!wasLoggedIn) {// Trennt die Verbindung, wenn der Nutzer nicht angemeldet war
                    repositoryServer.disconnect();
                }
            }
        } catch (InoNetException e) {
            throw new InnovatorConnectionException(FehlercodesInnovatorConnection.SERVERVERBINDUNG_ZUSAMMENGEBROCHEN, e.getMessage());
        } catch (SrvErrorException e) {
            throw new InnovatorConnectionException(FehlercodesInnovatorConnection.SERVERVERBINDUNG_ZUSAMMENGEBROCHEN, e.getMessage());
        }
    }

    /**
     * Baut die Verbindung zum Lizenzserver auf.
     * @param licSrv
     * @return
     */
    private LicenseServer connectLicenceServer() {
        try {
            LicenseServer licServ = new LicenseServer(hostname, port);
            Output.info("Host: " + hostname + "." + port);
            return licServ;
        } catch (InoNetException e) {
            String meldung = "Host: " + hostname + "." + port;
            throw new InnovatorConnectionException(FehlercodesInnovatorConnection.LIZENZ_SERVER_FEHLT, meldung);
        }
    }

    private RepositoryServer getRepositoryServer(String serverName, LicenseServer licenceServer) {
        try {
            Output.info("Versuche mit Repository '" + repositoryServerName + "' zu verbinden...");
            RepositoryServer repositoryServer = licenceServer.findServer(repositoryServerName);
            Output.info("Verbindung hergestellt mit Repository: " + repositoryServer.getName());

            return repositoryServer;
        } catch (InoNetException e) {
            String meldung = "Servername: " + serverName;
            Output.msg(LogMessageBuilder.createMessageOhneUUID(FehlercodesInnovatorConnection.SERVERNAME_FALSCH_GESCHRIEBEN, meldung));
            return findRepositoryServer(serverName, licenceServer);
        }

    }

    /**
     * Sucht den Repository-Server vom Lizenz-Server.
     * @param modelName2
     * @param repositoryServer
     * @return
     */
    private RepositoryServer findRepositoryServer(String serverName, LicenseServer licenceServer) {
        // licenceServer.findServer(repositoryServerName);
        List<RepositoryServer> repServerList;
        try {
            repServerList = licenceServer.getServerList();
            for (RepositoryServer repServer : repServerList) {
                if (repServer.getName().equalsIgnoreCase(serverName)) {
                    Output.info("Server: " + repServer.getName());
                    return repServer;
                }
            }
            // kein modell gefunden
            String meldung = "Server: " + serverName;
            throw new InnovatorConnectionException(FehlercodesInnovatorConnection.SERVER_FEHLT, meldung);
        } catch (InoNetException e) {
            e.printStackTrace();
            String meldung = "Host: " + hostname + "." + port + "; Repository-Server: " + serverName;
            throw new InnovatorConnectionException(FehlercodesInnovatorConnection.LIZENZ_SERVER_FEHLT, meldung);
        }
    }

    /**
     * Sucht das Modell vom Repository-Server.
     * @param modelName
     * @param repositoryServer
     * @return
     */
    private Model findModel(String modelName, RepositoryServer repositoryServer) {
        List<Model> modelList = repositoryServer.getModels();
        for (Model oneModel : modelList) {
            if (oneModel.getModelName().equalsIgnoreCase(modelName)) {
                return oneModel;
            }
        }
        // kein modell gefunden
        String meldung = "Modell: " + modelName;
        throw new InnovatorConnectionException(FehlercodesInnovatorConnection.MODELL_FEHLT, meldung);
    }

    /**
     * Loggt den Nutzer in das Modell ein.
     * @param model
     * @return
     * @throws InoNetException
     * @throws SrvErrorException
     */
    private boolean login(Model model) throws InoNetException, SrvErrorException {
        Output.info("Versuche Login auf Modell: " + model.getModelName());
        boolean reLoginSucceeded = tryReLogin(model);

        if (reLoginSucceeded) {
            Output.info("Sitzung übernommen: " + getLoginDisplayName(model));
        } else {
            tryLoginAsUserOrGuest(model);
            Output.info("Sitzung aufgebaut: " + getLoginDisplayName(model));
        }
        return reLoginSucceeded;
    }

    /**
     * @param model
     * @return
     * @throws InoNetException
     * @throws SrvErrorException
     */
    private String getLoginDisplayName(Model model) throws InoNetException, SrvErrorException {
        return model.getLogin().asExcellence().getLoginId().getDisplayName();
    }

    /**
     * Versucht eine Sitzung zu übernehmen.
     * @param model
     * @return Übernahme erfolgreich (true) sonst false
     */
    private boolean tryReLogin(Model model) {
        try {
            return model.tryReLogin(user);
        } catch (Exception e) {
            // catch Exception und nicht catch InoNlsException,
            // da fälschlicherweise auch eine NullPointerException geworfen wird (Programmierfehler in Innovator API)
            return false;
        }
    }

    /**
     * Loggt sich in das Modell ein und liefert eine Exception, wenn kein Login möglich ist.
     * @param model
     * @return
     * @throws InoNetException
     * @throws SrvErrorException
     */
    private void tryLoginAsUserOrGuest(Model model) throws InoNetException, SrvErrorException {
        try {
            model.loginUser(user, password, rolle);
        } catch (SrvErrorException e1) {
            model.loginModelGuest("");
        }
    }
}