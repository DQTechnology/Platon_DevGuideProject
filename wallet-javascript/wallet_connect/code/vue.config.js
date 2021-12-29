const path = require("path");

module.exports = {
    pluginOptions: {
        "style-resources-loader": {
            preProcessor: "less",
            patterns: [path.resolve(__dirname, "src/assets/less/variable.less")]
        }
    },
    pages: {
        popup: {
            entry: "src/main.js",
            template: "public/popup.html"
        },

        home: {
            entry: "src/main.js",
            template: "public/home.html"
        }
    }
};
