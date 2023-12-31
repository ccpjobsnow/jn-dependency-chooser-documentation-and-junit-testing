const path = require('path');
const webpack = require('webpack');

const CleanWebpackPlugin = require('clean-webpack-plugin');
const CopyWebpackPlugin = require('copy-webpack-plugin');

const HtmlWebpackPlugin = require('html-webpack-plugin');

const srcRoot = path.resolve(__dirname, 'src');
const appRoot = path.resolve(srcRoot, 'app');

const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

module.exports = (env) => {

  const isDev = env == 'development';

  return {
    context: path.resolve(__dirname),
    entry: {
      main: './src/app/main.js',
      vendor: [
        'react', 'react-dom', 'jquery', 'moment', 'react-table',
        // 'jquery-ui', 'bootstrap',
        'react-bootstrap', 'lodash'
      ]
    },
    output: {
      path: path.resolve(__dirname, './dist'),
      filename: isDev ? 'js/[name].bundle.js' : 'js/[name].[hash].bundle.js',
      sourceMapFilename: isDev ? 'js/[name].bundle.map' : 'js/[name].[chunkhash].bundle.map',
      chunkFilename: isDev ? 'js/[id].chunk.js' : 'js/[id].[chunkhash].chunk.js',


      publicPath: '/'
    },
    module: {
      rules: [
        {

          test: /\.(woff2|woff|ttf|eot|svg|png|jpe?g|svg|ico)(\?v=[a-z0-9]\.[a-z0-9]\.[a-z0-9])?$/,
          use: [
              {
                  loader: 'url-loader?limit=100000'
              }
          ]
      },
        {
          test: /\.jsx?$/, // A regexp to test the require path. accepts either js or jsx

          loader: 'babel-loader',

          query:{
            "presets": [
              ["es2015", {"modules": false}],
              //Webpack understands the native import syntax, and uses it for tree shaking

              "stage-2",
              //Specifies what level of language features to activate.
              //State 2 is "draft", 4 is finished, 0 is strawman.
              //See https://tc39.github.io/process-document/

              "react"
              //Transpile React components to JS
            ],
            "plugins": [
              "react-hot-loader/babel"
              //Enables React code to work with HMR.
            ]
          },

          exclude: [
            /node_modules/
          ],
        },

        {test: /\.css$/, loader: "style-loader!css-loader"},

        {test: /\.json$/, loader: "json-loader"},

        {
          test: /\.(jpe?g|png|gif)$/,
          loader: 'file-loader',
          query:{
            name: 'assets/img/[name].[ext]'
          }
        },
      ]

    },

    // externals: ["fs"],
    resolve: {
      extensions: [".js", ".jsx"],

      modules: [
        appRoot,
        'node_modules'
      ]
    },
    devServer: {

      // historyApiFallback: true,
      contentBase: path.join(__dirname, "dist"),
      port: 2200,
      // hot: true,
      compress:true,
      publicPath: '/',
      stats: "minimal"

    },
    stats: "minimal",
    performance: {
      hints: false
    },
    // devtool: isDev ? 'eval' : 'source-map', //false, //isDev ? 'eval' : 'cheap-source-map',
    devtool: isDev ? 'eval' : 'cheap-source-map', //false, //isDev ? 'eval' : 'cheap-source-map',

    plugins: [
      new CleanWebpackPlugin(['dist']),
      new CopyWebpackPlugin([
        {from: './src/index.html'},
        {from: './src/assets', to: './assets'},

      ]),
      new webpack.IgnorePlugin(/^\.\/locale$/, /moment$/),

      new HtmlWebpackPlugin({
        template: path.resolve(srcRoot, 'index.html'),
        chunksSortMode: 'dependency'
      }),

      new webpack.optimize.CommonsChunkPlugin({
        name: 'vendor',
        filename: 'js/[hash].vendor.js',

        minChunks: Infinity,
      }),

      new webpack.DefinePlugin({
        process: {
          env: {
            NODE_ENV: isDev ? '"development"' : '"production"'
          }
        },
        ENV_URL: isDev ? '"valid"' : '"prod"'
      }),


    ].concat(
      !isDev
        ? // production only plugins
        [
          // o original nao suporta ES6
          new UglifyJsPlugin(),
//          new webpack.optimize.UglifyJsPlugin(),
          // new webpack.optimize.UglifyJsPlugin({
            // compress: {
            //   warnings: false
            // }
          //   sourceMap: true,   // enable source maps to map errors (stack traces) to modules
          //   output: {
          //     comments: false, // remove all comments
          //   },
          // }),
        ]
        :// dev only plugins
        []
    )
  }
};
