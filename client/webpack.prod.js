const webpack = require('webpack');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');

module.exports = merge(common, {
  resolve: {
    alias: {
      vue: 'vue/dist/vue.min.js'
      moment: 'moment/min/moment.min.js'
      "chart.js": 'chart.js/dist/Chart.bundle.min.js'
    }
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify("production")
      },
      PRODUCTION: JSON.stringify(true)
    })
  ]
});
