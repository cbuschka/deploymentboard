const defaultConfig = {
    webapiBaseUrl: window.location.origin,
    appBaseUrl: window.location.origin
};

const envConfigs = {
    dev: {
        regex: '.*localhost.*',
        config: {}
    },
    prod: {
        regex: '.*example.com.*',
        config: {
            webapiBaseUrl: "https://example.com",
            appBaseUrl: "https://example.com"
        }
    }
};

function getMatchingConfig() {
    const envNames = Object.keys(envConfigs);
    for (let i = 0; i < envNames.length; ++i) {
        const envName = envNames[i];
        const envConfig = envConfigs[envName];
        if (window.location.origin.match(envConfig.regex)) {
            return envConfig.config || {};
        }
    }

    return {};
}

export const config = Object.assign({}, defaultConfig, getMatchingConfig());
