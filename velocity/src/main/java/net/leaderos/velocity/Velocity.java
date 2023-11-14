package net.leaderos.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.event.proxy.ProxyShutdownEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import eu.okaeri.configs.ConfigManager;
import eu.okaeri.configs.yaml.snakeyaml.YamlSnakeYamlConfigurer;
import lombok.Getter;
import lombok.Setter;
import net.leaderos.shared.Shared;
import net.leaderos.shared.helpers.UpdateUtil;
import net.leaderos.velocity.api.ModuleManager;
import net.leaderos.velocity.commands.LeaderOSCommand;
import net.leaderos.velocity.configuration.Config;
import net.leaderos.velocity.configuration.Language;
import net.leaderos.velocity.configuration.Modules;
import net.leaderos.velocity.modules.auth.AuthModule;
import net.leaderos.velocity.modules.connect.ConnectModule;
import net.leaderos.velocity.modules.credit.CreditModule;
import net.leaderos.velocity.modules.discord.DiscordModule;
import org.slf4j.Logger;

import java.io.File;
import java.nio.file.Path;

/**
 * Main class of velocity
 * @author poyrazinan
 * @since 1.0
 */
@Getter
@Setter
@Plugin(
        id = "leaderos",
        name = "LeaderOS",
        version = "1.0.2",
        url = "https://leaderos.net",
        description = "LeaderOS Plugin for Velocity",
        authors = {"leaderos"}
)
public class Velocity {

    /**
     * Instance of server
     */
    private final ProxyServer server;

    /**
     * Instance of plugin
     */
    @Getter
    private static Velocity instance;

    /**
     * Logger of server
     */
    private final Logger logger;

    /**
     * Data directory of server
     */
    private final Path dataDirectory;

    /**
     * Config file of plugin
     */
    private Config configFile;

    /**
     * Lang file of plugin
     */
    private Language langFile;

    /**
     * Module file of plugin
     */
    private Modules modulesFile;

    /**
     * Module manager holder
     */
    private ModuleManager moduleManager;

    @Getter
    private CommandManager commandManager;

    /**
     * Shared holder
     */
    private Shared shared;

    /**
     * Constructor of main class
     * @param server proxyserver
     * @param logger logger class
     * @param dataDirectory data path
     */
    @Inject
    public Velocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        instance = this;
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    /**
     * onEnable event of velocity
     * @param event of startup
     */
    @Subscribe
    public void onProxyInitialize(ProxyInitializeEvent event) {
        commandManager = getServer().getCommandManager();
        setupFiles();
        new UpdateUtil("1.0.2");
        this.shared = new Shared(getConfigFile().getSettings().getUrl(),
                getConfigFile().getSettings().getApiKey());
        this.moduleManager = new ModuleManager();
        getModuleManager().registerModule(new CreditModule());
        getModuleManager().registerModule(new AuthModule());
        getModuleManager().registerModule(new DiscordModule());
        getModuleManager().registerModule(new ConnectModule());
        getModuleManager().enableModules();
        CommandMeta commandMeta = Velocity.getInstance().getCommandManager().metaBuilder("leaderos")
                .plugin(Velocity.getInstance())
                .build();
        Velocity.getInstance().getCommandManager().register(commandMeta, new LeaderOSCommand());
    }

    /**
     * onDisable event of velocity
     * @param event of disable
     */
    @Subscribe
    public void onProxyShutdown(ProxyShutdownEvent event) {
        getModuleManager().disableModules();
    }


    /**
     * Setups config, lang and modules file file
     */
    public void setupFiles() {
        try {
            this.configFile = ConfigManager.create(Config.class, (it) -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile(new File(getDataDirectory().toFile().getAbsolutePath(), "config.yml"));
                it.saveDefaults();
                it.load(true);
            });
            this.modulesFile = ConfigManager.create(Modules.class, (it) -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile(new File(getDataDirectory().toFile().getAbsolutePath(), "modules.yml"));
                it.saveDefaults();
                it.load(true);
            });
            String langName = configFile.getSettings().getLang();
            Class langClass = Class.forName("net.leaderos.velocity.configuration.lang." + langName);
            Class<Language> languageClass = langClass;
            this.langFile = ConfigManager.create(languageClass, (it) -> {
                it.withConfigurer(new YamlSnakeYamlConfigurer());
                it.withBindFile(new File(getDataDirectory().toFile().getAbsolutePath() + "/lang", langName + ".yml"));
                it.saveDefaults();
                it.load(true);
            });
        } catch (Exception exception) {
            throw new RuntimeException("Error loading config.yml");
        }
    }
}