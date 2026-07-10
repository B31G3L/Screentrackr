module.exports = {
	globDirectory: './',
	globPatterns: [
		'**/*.{css,ico,png,js,html,json,txt}'
	],
	globIgnores: [
		'sw.js',
		'workbox-config.js',
		'**/node_modules/**'
	],
	swDest: 'sw.js',
	ignoreURLParametersMatching: [
		/^utm_/,
		/^fbclid$/
	]
};