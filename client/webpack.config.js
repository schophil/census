var path = require('path');
var webpack = require('webpack');

var env = process.env.NODE_ENV;

var config = {
  entry: './src/app/app.js',
  output: {
    filename: 'bundle.js',
    path: path.resolve(__dirname, 'dist')
  },
  resolve: {
    alias: {
    }
  },
  module: {
    rules: [
      {
        test: /\.css$/,
        use: [ 'style-loader', 'css-loader' ]
      },
      {
        test: /\.(png|jpg|gif|svg|eot|ttf|woff|woff2)$/,
        loader: 'url-loader',
        options: {
          limit: 10000
        }
      },
      {
        test: /\.less$/,
        use: [{
          loader: "style-loader" // creates style nodes from JS strings
        }, {
          loader: "css-loader" // translates CSS into CommonJS
        }, {
          loader: "less-loader" // compiles Less to CSS
        }]
      }
    ]
  },
  plugins: [
    new webpack.DefinePlugin({
      'process.env': {
        NODE_ENV: JSON.stringify("production")
      },
       PRODUCTION: JSON.stringify(true)
    })
  ],
  target: 'web',
  devServer: {
    contentBase: [path.join(__dirname, "dist"), path.join(__dirname, "src")],
    port: 9000
  }
};

if (env === 'production') {
  config.resolve.alias.vue = 'vue/dist/vue.min.js'
  config.resolve.alias.moment = 'moment/min/moment.min.js'
  config.resolve.alias["chart.js"] = 'chart.js/dist/Chart.bundle.min.js'
} else {
  config.resolve.alias.vue = 'vue/dist/vue.js'
}

module.exports = config;
