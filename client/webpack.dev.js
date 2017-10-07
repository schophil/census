const webpack = require('webpack');
const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
  resolve: {
    alias: {
      vue: 'vue/dist/vue.js'
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify("development")
      },
      PRODUCTION: JSON.stringify(false)
    })
  ],
  devServer: {
    contentBase: [ path.join(__dirname, "src"), path.join(__dirname, "dist") ],
    port: 9000
  }
});
