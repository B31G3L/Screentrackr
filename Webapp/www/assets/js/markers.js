// Get references to DOM elements for user controls and the preview area
const brightnessInput = document.getElementById("brightnessInput"), // Range input for brightness
  brightnessLabel = document.getElementById("brightnessLabel"), // Label to display brightness value
  BGColor = document.getElementById("BGColor"), // Color input for background
  MarkerColor = document.getElementById("MarkerColor"), // Color input for markers
  preview = document.getElementById("preview"), // Div element to display the marker preview
  startButton = document.getElementById("startButton"), // Button to start fullscreen preview
  MarkerSize = document.getElementById("MarkerSize"), // Select dropdown for main marker size
  MarkerDensity = document.getElementById("MarkerDensity"), // Select dropdown for marker density
  MarkerType = document.getElementById("MarkerType"), // Select dropdown for marker shape/type
  edgemarkers = document.getElementById("edgemarkers"), // Select dropdown for edge marker type
  EdgeMarkerSize = document.getElementById('EdgeMarkerSize'), // Select dropdown for edge marker size
  ScrollingMarkers = document.getElementById('ScrollingMarkers'), // Select dropdown for scrolling markers
  ScrollingMarkerType = document.getElementById('ScrollingMarkerType'), // Select dropdown for scrolling marker shape
  ScrollingMarkerSize = document.getElementById('ScrollingMarkerSize'), // Select dropdown for scrolling marker size
  configPanel = document.getElementById('config-panel'); // The panel for UI controls

// Define SVG strings for different marker shapes
const marker_circle = '<svg width="100%" height="100%" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;fill:white;"><g transform="matrix(3.81387,0,0,3.81387,-278.467,-4439.58)"><circle cx="106.576" cy="1197.62" r="33.562"/></g></svg>',
  marker_cross = '<svg width="100%" height="100%" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;fill:white;"><path d="M162.909,93.091L162.909,0L93.091,0L93.091,93.091L0,93.091L0,162.909L93.091,162.909L93.091,256L162.909,256L162.909,162.909L256,162.909L256,93.091L162.909,93.091Z"/></svg>',
  marker_pie = '<svg width="100%" height="100%" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;fill:white;"><g transform="matrix(3.45526,0,0,3.45526,-285.507,-4720.46)"><g transform="matrix(1,0,0,1,-31.5713,-51.445)"><path d="M151.246,1417.61C130.787,1417.61 114.201,1434.2 114.201,1454.66L151.246,1454.66L151.246,1417.61Z"/></g><g transform="matrix(-1,-1.22465e-16,1.22465e-16,-1,270.921,2857.87)"><path d="M151.246,1417.61C130.787,1417.61 114.201,1434.2 114.201,1454.66L151.246,1454.66L151.246,1417.61Z"/></g></g></svg>',
  marker_triangle = '<svg width="100%" height="100%" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;fill:white;"><g transform="matrix(3.60034,0,0,3.60034,-283.163,-4548.23)"><path d="M114.201,1263.28L149.753,1334.38L78.649,1334.38L114.201,1263.28Z" style=""/></g></svg>',
  marker_plus = '<svg width="100%" height="100%" viewBox="0 0 256 256" version="1.1" xmlns="http://www.w3.org/2000/svg" xmlns:xlink="http://www.w3.org/1999/xlink" xml:space="preserve" xmlns:serif="http://www.serif.com/" style="fill-rule:evenodd;clip-rule:evenodd;stroke-linejoin:round;stroke-miterlimit:2;fill:white;"><path d="M134,122L134,0L122,0L122,122L0,122L0,134L122,134L122,256L134,256L134,134L256,134L256,122L134,122Z"/></svg>';

// --- Data-driven patterns for marker creation ---
const markerPatterns = {
  density1: (offset) => [
    { top: offset, left: offset, transform: "translate(-50%, -50%)" },
    { top: offset, right: offset, transform: "translate(50%, -50%)" },
    { bottom: offset, left: offset, transform: "translate(-50%, 50%)" },
    { bottom: offset, right: offset, transform: "translate(50%, 50%)" },
    { top: "50%", left: "50%", transform: "translate(-50%, -50%)" },
  ],
  density2: (offset) => [
    { top: "50%", right: offset, transform: "translate(50%, -50%)" },
    { top: "50%", left: offset, transform: "translate(-50%, -50%)" },
    { top: offset, left: "50%", transform: "translate(-50%, -50%)" },
    { bottom: offset, left: "50%", transform: "translate(-50%, 50%)" },
  ],
  density3: () => [
    { top: "25%", left: "25%", transform: "translate(-50%, -50%)" },
    { top: "25%", left: "75%", transform: "translate(-50%, -50%)" },
    { top: "75%", left: "25%", transform: "translate(-50%, -50%)" },
    { top: "75%", left: "75%", transform: "translate(-50%, -50%)" },
  ],
};

const edgeMarkerPattern = [
    { top: "0%", left: "0%", transform: "translate(-50%, -50%)" },
    { top: "0%", right: "0%", transform: "translate(50%, -48%)" },
    { bottom: "0%", left: "0%", transform: "translate(-50%, 50%)" },
    { bottom: "0%", right: "0%", transform: "translate(50%, 48%)" },
];

// Variables to store the current SVG path for edge markers and main markers, and marker width
let imgPathEdge = marker_cross; // Default edge marker is a cross
let imgPath = ""; // Will hold the SVG for the currently selected main marker type
let width = ""; // Will hold the CSS width percentage for main markers

// --- Global State ---
let isInActivePreviewMode = false; // Tracks if the preview is active and UI is hidden

// --- Wake Lock ---
let wakeLock = null;

async function requestWakeLock() {
  if (!('wakeLock' in navigator)) return;
  try {
    wakeLock = await navigator.wakeLock.request('screen');
  } catch (err) {
    // Silently ignore — system denied (e.g. low battery, power save mode)
  }
}

async function releaseWakeLock() {
  if (wakeLock) {
    await wakeLock.release();
    wakeLock = null;
  }
}

// Re-acquire wake lock if the tab becomes visible again while markers are active
document.addEventListener('visibilitychange', () => {
  if (document.visibilityState === 'visible' && isInActivePreviewMode) {
    requestWakeLock();
  }
});

// --- Scrolling Markers Variables ---
let scrollingMarkerContainer = null; // Container div for scrolling markers
let scrollingMarkerElements = []; // Array to store scrolling marker elements and their base positions
let currentScrollX = 0; // Current horizontal offset for scrolling markers
let currentScrollY = 0; // Current vertical offset for scrolling markers
let touchStartX = 0; // Starting X position for touch events
let touchStartY = 0; // Starting Y position for touch events
let isDragging = false; // Flag to track touch dragging state

// --- Momentum Scrolling Variables ---
let velocityX = 0; // Current horizontal velocity (pixels per millisecond)
let velocityY = 0; // Current vertical velocity (pixels per millisecond)
let lastMoveTime = 0; // Timestamp of the last touchmove event
let isMomentumScrolling = false; // Flag indicating if momentum animation is active
let animationFrameId = null; // ID for cancelling requestAnimationFrame
const dampingFactor = 0.95; // Friction factor (closer to 1 = less friction)
const minVelocity = 0.1; // Velocity below which momentum stops
let scrollingMarkerContainerResizeObserver = null; // For observing size changes of the scrolling marker container
let lastFrameTime = 0; // Timestamp for calculating frame delta time

// --- Settings Persistence ---
const SETTINGS_KEY = 'screentrackr_settings';

function saveSettings() {
    const settings = {
        brightness: brightnessInput.value,
        bgColor: BGColor.value,
        markerColor: MarkerColor.value,
        markerDensity: MarkerDensity.value,
        markerSize: MarkerSize.value,
        markerType: MarkerType.value,
        edgeMarkers: edgemarkers.value,
        edgeMarkerSize: EdgeMarkerSize.value,
        scrollingMarkers: ScrollingMarkers.value,
        scrollingMarkerType: ScrollingMarkerType.value,
        scrollingMarkerSize: ScrollingMarkerSize.value
    };
    try {
        localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
    } catch (e) {
        // localStorage may be unavailable (e.g. private browsing on some browsers)
    }
}

function loadSettings() {
    try {
        const raw = localStorage.getItem(SETTINGS_KEY);
        if (!raw) return;
        const settings = JSON.parse(raw);
        if (settings.brightness != null) brightnessInput.value = settings.brightness;
        if (settings.bgColor != null) BGColor.value = settings.bgColor;
        if (settings.markerColor != null) MarkerColor.value = settings.markerColor;
        if (settings.markerDensity != null) MarkerDensity.value = settings.markerDensity;
        if (settings.markerSize != null) MarkerSize.value = settings.markerSize;
        if (settings.markerType != null) MarkerType.value = settings.markerType;
        if (settings.edgeMarkers != null) edgemarkers.value = settings.edgeMarkers;
        if (settings.edgeMarkerSize != null) EdgeMarkerSize.value = settings.edgeMarkerSize;
        if (settings.scrollingMarkers != null) ScrollingMarkers.value = settings.scrollingMarkers;
        if (settings.scrollingMarkerType != null) ScrollingMarkerType.value = settings.scrollingMarkerType;
        if (settings.scrollingMarkerSize != null) ScrollingMarkerSize.value = settings.scrollingMarkerSize;
    } catch (e) {
        // Ignore corrupted or unavailable storage
    }
}

// --- Helper: resolve marker type string to SVG ---
function getMarkerSvg(typeValue) {
    switch (typeValue) {
        case "circle": return marker_circle;
        case "cross": return marker_cross;
        case "pie": return marker_pie;
        case "triangle": return marker_triangle;
        case "plus": return marker_plus;
        default: return marker_plus;
    }
}

// --- Helper: resolve marker size string to CSS width ---
function getMarkerWidth(sizeValue) {
    switch (sizeValue) {
        case "1": return "1%";
        case "2": return "2%";
        case "3": return "3%";
        case "4": return "6%";
        case "5": return "9%";
        default: return "2%";
    }
}

// --- Toggle visibility of scrolling-specific options ---
function toggleScrollingOptions() {
    const visible = ScrollingMarkers.value !== 'none';
    document.querySelectorAll('.scrolling-options').forEach(row => {
        row.style.display = visible ? '' : 'none';
    });
}

// --- Initial Setup ---
loadSettings();
toggleScrollingOptions();
brightnessLabel.textContent = "Overall brightness: " + brightnessInput.value;
preview.style.backgroundColor = BGColor.value;

// Config panel styles are defined in CSS via #config-panel selector

// The body should be transparent by default to allow the preview (z-index: -1) to be visible as the background.
// UI elements will sit on top of the preview. If UI elements need their own distinct background,
// it should be applied to a specific container div for the UI, not the entire body.
updatePreview();

// --- Fullscreen Change Handling ---
/**
 * Handles changes in the document's fullscreen state.
 * Ensures markers are redrawn and UI state is consistent.
 */
function handleFullscreenChange() {
    const isCurrentlyFullscreen = !!(document.fullscreenElement ||
                                   document.webkitFullscreenElement ||
                                   document.mozFullScreenElement ||
                                   document.msFullscreenElement);

    if (!isCurrentlyFullscreen && isInActivePreviewMode) {
        // Exited fullscreen (e.g., via Esc key or browser UI) while markers were active.
        // We should revert to the non-active preview state and show the config panel.
        console.log('Exited fullscreen externally. Restoring UI and setting preview to background.');
        isInActivePreviewMode = false;
        releaseWakeLock();
        if (configPanel) {
            configPanel.classList.remove('config-panel-hidden');
        }
        // Stop momentum scrolling if it was active when fullscreen was exited
        if (isMomentumScrolling) {
            cancelAnimationFrame(animationFrameId);
            isMomentumScrolling = false;
            velocityX = 0;
            velocityY = 0;
            console.log("Momentum stopped due to external fullscreen exit.");
        }
    }
    // Always update the preview to ensure markers are correctly sized for the new viewport dimensions.
    // updatePreview() will use the current (potentially updated) isInActivePreviewMode.
    console.log('Fullscreen state changed. Requesting preview update.');
    requestAnimationFrame(updatePreview);
}

// Listen for fullscreen changes on the document
document.addEventListener('fullscreenchange', handleFullscreenChange);
document.addEventListener('webkitfullscreenchange', handleFullscreenChange); // Safari, Chrome
document.addEventListener('mozfullscreenchange', handleFullscreenChange);    // Firefox
document.addEventListener('MSFullscreenChange', handleFullscreenChange);     // IE11/Edge (legacy)

// --- Event Listeners ---
// --- Lightweight listeners (no marker rebuild needed) ---
brightnessInput.addEventListener('input', function () {
  brightnessLabel.textContent = "Overall brightness: " + brightnessInput.value;
  preview.style.filter = "brightness(" + brightnessInput.value + "%)";
  saveSettings();
}, false);
BGColor.addEventListener('input', function () { preview.style.backgroundColor = BGColor.value; saveSettings(); }, false);
MarkerColor.addEventListener('input', function () { changeMarkerColor(); saveSettings(); });

// --- Listeners that require full marker rebuild ---
MarkerDensity.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
MarkerSize.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
edgemarkers.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
EdgeMarkerSize.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
ScrollingMarkers.addEventListener('change', function () { toggleScrollingOptions(); updatePreview(); saveSettings(); }, false);
MarkerType.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
ScrollingMarkerType.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
ScrollingMarkerSize.addEventListener('change', function () { updatePreview(); saveSettings(); }, false);
startButton.addEventListener('click', startPreSet);

// --- Keyboard Shortcuts ---
document.addEventListener('keydown', function (event) {
  // +/= key increases brightness, - key decreases brightness
  if (event.key === '+' || event.key === '=') {
    brightnessInput.value = Math.min(200, parseInt(brightnessInput.value, 10) + 5);
    brightnessLabel.textContent = "Overall brightness: " + brightnessInput.value;
    preview.style.filter = "brightness(" + brightnessInput.value + "%)";
    saveSettings();
  } else if (event.key === '-') {
    brightnessInput.value = Math.max(1, parseInt(brightnessInput.value, 10) - 5);
    brightnessLabel.textContent = "Overall brightness: " + brightnessInput.value;
    preview.style.filter = "brightness(" + brightnessInput.value + "%)";
    saveSettings();
  }
});

// --- Scrolling Event Listeners (Mouse Wheel) ---
preview.addEventListener('wheel', function (event) {
  const scrollMode = ScrollingMarkers.value;
  if (scrollMode === 'none' || !scrollingMarkerContainer || !isInActivePreviewMode) return; // Only scroll if active

  // Stop any momentum scrolling if wheel is used
  if (isMomentumScrolling) {
    cancelAnimationFrame(animationFrameId);
    isMomentumScrolling = false;
    velocityX = 0;
    velocityY = 0;
  }

  event.preventDefault();

  const scrollSensitivity = 15;

  if (scrollMode === 'vertical') {
    const delta = Math.max(-1, Math.min(1, -event.deltaY));
    currentScrollY += delta * scrollSensitivity;
  } else if (scrollMode === 'horizontal') {
    // Prefer deltaX (e.g. trackpad horizontal swipe), fall back to deltaY for normal scroll wheels
    const rawDelta = event.deltaX !== 0 ? event.deltaX : event.deltaY;
    const delta = Math.max(-1, Math.min(1, -rawDelta));
    currentScrollX += delta * scrollSensitivity;
  }

  updateScrollingMarkerPositions();

}, { passive: false });


// --- Scrolling Event Listeners (Touch) ---
preview.addEventListener('touchstart', function (event) {
  const scrollMode = ScrollingMarkers.value;
  if (scrollMode === 'none' || !scrollingMarkerContainer || !isInActivePreviewMode) return; // Only scroll if active

  // Stop any existing momentum scrolling
  if (isMomentumScrolling) {
    cancelAnimationFrame(animationFrameId);
    isMomentumScrolling = false;
  }

  if (event.touches.length === 1) {
    isDragging = true;
    touchStartX = event.touches[0].clientX;
    touchStartY = event.touches[0].clientY;

    // Reset velocity tracking
    velocityX = 0;
    velocityY = 0;
    lastMoveTime = performance.now(); // Use high-resolution timer

    // Prevent text selection during drag
    preview.style.userSelect = 'none';
  }
}, { passive: false }); // Use passive: false to ensure touchmove preventDefault works reliably

preview.addEventListener('touchmove', function (event) {
  const scrollMode = ScrollingMarkers.value;
  if (scrollMode === 'none' || !isDragging || event.touches.length !== 1 || !scrollingMarkerContainer || !isInActivePreviewMode) return;

  event.preventDefault(); // Prevent default page scroll/zoom during marker drag

  const touchCurrentX = event.touches[0].clientX;
  const touchCurrentY = event.touches[0].clientY;
  const deltaX = touchCurrentX - touchStartX;
  const deltaY = touchCurrentY - touchStartY;

  const currentTime = performance.now();
  const deltaTime = currentTime - lastMoveTime;

  // Update scroll position immediately
  if (scrollMode === 'vertical') {
    currentScrollY += deltaY;
  } else if (scrollMode === 'horizontal') {
    currentScrollX += deltaX;
  }

  // Calculate velocity (pixels per millisecond)
  if (deltaTime > 0) {
      if (scrollMode === 'vertical') {
          velocityX = 0; // Ensure velocity is zero on the non-scrolling axis
          velocityY = deltaY / deltaTime;
      } else if (scrollMode === 'horizontal') {
          velocityX = deltaX / deltaTime;
          velocityY = 0; // Ensure velocity is zero on the non-scrolling axis
      }
  }

  // Update positions for next frame calculation
  touchStartX = touchCurrentX;
  touchStartY = touchCurrentY;
  lastMoveTime = currentTime;

  updateScrollingMarkerPositions();

}, { passive: false }); // Use passive: false for touchmove to allow preventDefault

preview.addEventListener('touchend', function () {
  const scrollMode = ScrollingMarkers.value;
  if (scrollMode === 'none' || !scrollingMarkerContainer || !isInActivePreviewMode) return;

  if (isDragging) {
    isDragging = false;
    // Allow text selection again
    preview.style.userSelect = '';

    // Start momentum scrolling if velocity is significant
    if (Math.abs(velocityX) > minVelocity || Math.abs(velocityY) > minVelocity) {
      isMomentumScrolling = true;
      lastFrameTime = performance.now(); // Initialize frame time
      animationFrameId = requestAnimationFrame(momentumLoop);
    } else {
        // Ensure velocity is zero if below threshold
        velocityX = 0;
        velocityY = 0;
    }
  }
}, { passive: true }); // Use passive: true for touchend

preview.addEventListener('touchcancel', function () {
    // Treat touchcancel the same as touchend for cleanup
    const scrollMode = ScrollingMarkers.value;
    if (scrollMode === 'none' || !scrollingMarkerContainer || !isInActivePreviewMode) return;

    if (isDragging) {
        isDragging = false;
        preview.style.userSelect = '';
        // Optionally stop momentum on cancel, or let it play out
        // For now, let's stop it for safety
        velocityX = 0;
        velocityY = 0;
        if (isMomentumScrolling) {
            cancelAnimationFrame(animationFrameId);
            isMomentumScrolling = false;
        }
    }
}, { passive: true });


// --- Momentum Scrolling Loop ---
function momentumLoop() {
  if (!isMomentumScrolling) return; // Exit if momentum was stopped externally

  const currentTime = performance.now();
  const deltaTime = currentTime - lastFrameTime;

  // Only update if deltaTime is reasonable (e.g., avoid huge jumps if tab was inactive)
  if (deltaTime > 0 && deltaTime < 100) {
      // Update scroll position based on velocity
      currentScrollX += velocityX * deltaTime;
      currentScrollY += velocityY * deltaTime;

      // Apply damping (friction)
      velocityX *= dampingFactor;
      velocityY *= dampingFactor;

      // Update marker visuals
      updateScrollingMarkerPositions();
  }

  lastFrameTime = currentTime;

  // Check if velocity is still significant
  if (Math.abs(velocityX) > minVelocity || Math.abs(velocityY) > minVelocity) {
    // Continue the loop
    animationFrameId = requestAnimationFrame(momentumLoop);
  } else {
    // Stop the loop
    isMomentumScrolling = false;
    velocityX = 0;
    velocityY = 0;
  }
}

// --- Helper Functions ---

/**
 * Creates an SVG DOM element from an SVG string and applies a fill color.
 * @param {string | null} svgString The string representation of the SVG.
 * @param {string} color The fill color to apply.
 * @returns {SVGElement | null} The created SVG element or null if creation fails or svgString is null.
 */
function createStyledSvgElement(svgString, color) {
    if (!svgString) {
        return null;
    }
    const tempDiv = document.createElement('div');
    tempDiv.innerHTML = svgString; // Safely parse the SVG string
    const svgElement = tempDiv.querySelector('svg');

    if (svgElement) {
        svgElement.style.display = 'block'; // Ensure SVG behaves as a block element
        svgElement.style.width = '100%';    // Ensure SVG fills its wrapper width
        svgElement.style.height = '100%';   // Ensure SVG fills its wrapper height
        // Apply fill color to relevant shapes or the SVG root
        const shapes = svgElement.querySelectorAll('path, circle, rect, polygon'); // Common SVG shapes
        if (shapes.length > 0) {
            shapes.forEach(shape => shape.style.fill = color);
        } else {
            svgElement.style.fill = color; // Fallback for SVGs where fill is on the root
        }
        return svgElement;
    }
    return null; // SVG string could not be parsed into an SVG element
}

/**
 * Updates the position of individual scrolling markers using modulo arithmetic and CSS transforms for performance.
 */
function updateScrollingMarkerPositions() {
  if (!scrollingMarkerContainer || scrollingMarkerElements.length === 0) return;

  // Get container dimensions ONCE per update, slightly more efficient
  const containerWidth = scrollingMarkerContainer.offsetWidth;
  const containerHeight = scrollingMarkerContainer.offsetHeight;

  // Avoid division by zero if container is not rendered yet or hidden
  if (containerWidth <= 0 || containerHeight <= 0) {
    // console.warn("updateScrollingMarkerPositions called with zero/negative container dimensions.");
    return;
  }

  scrollingMarkerElements.forEach(markerData => {
    // Calculate base position in pixels
    const baseX = markerData.baseLeftPercent * containerWidth;
    const baseY = markerData.baseTopPercent * containerHeight;

    // Calculate raw scrolled position
    const rawX = baseX + currentScrollX;
    const rawY = baseY + currentScrollY;

    // Apply modulo for wrapping (ensuring positive result)
    // ((n % m) + m) % m handles negative results correctly
    const wrappedX = ((rawX % containerWidth) + containerWidth) % containerWidth;
    const wrappedY = ((rawY % containerHeight) + containerHeight) % containerHeight;

    // Apply the calculated pixel position using transform for better performance
    markerData.element.style.transform = `translate(${wrappedX}px, ${wrappedY}px) translate(-50%, -50%)`;
  });
}


/**
 * Changes the fill color of all SVG elements within the preview.
 */
function changeMarkerColor() {
  Array.from(preview.querySelectorAll(".marker > svg")).forEach(function (svgElement) {
    // Find the actual path/shape element(s) inside the SVG to apply fill
    const shapes = svgElement.querySelectorAll('path, circle, rect, polygon'); // Add other shapes if needed
    if (shapes.length > 0) {
        shapes.forEach(shape => shape.style.fill = MarkerColor.value);
    } else {
        // Fallback for simple SVGs where fill might be on the root
        svgElement.style.fill = MarkerColor.value;
    }
  });
}

/**
 * Creates a single marker element and adds it to the specified container.
 * (Used for static screen and edge markers).
 */
function addMarkerToContainer(container, imgPath, top, left, bottom, right, transform, widthValue, markerClasses) {
    const markerWrapper = document.createElement('div');
    markerWrapper.classList.add(...markerClasses);
    markerWrapper.style.position = 'absolute';
    // Ensure pointer events pass through the marker element itself
    markerWrapper.style.pointerEvents = 'none';
    markerWrapper.style.width = widthValue;
    // Eliminate potential spacing issues from text/line-height properties
    // by ensuring the wrapper itself doesn't contribute to layout based on font metrics.
    markerWrapper.style.fontSize = '0';
    markerWrapper.style.lineHeight = '0';

    const svgElement = createStyledSvgElement(imgPath, MarkerColor.value);

    if (svgElement) {
        markerWrapper.appendChild(svgElement); // Append the actual SVG element
    } else if (imgPath) { // Only log or add fallback if imgPath was provided but failed
        console.warn(`Could not create SVG for marker. imgPath type: ${typeof imgPath}, first 50 chars: ${String(imgPath).substring(0,50)}`);
        // Optionally, add a text placeholder or a default visual indicator if SVG creation fails
        // markerWrapper.textContent = '?'; // Example placeholder
    }

    if (top !== null) markerWrapper.style.top = top;
    if (left !== null) markerWrapper.style.left = left;
    // Note: 'bottom' and 'right' positioning can sometimes be less predictable with transforms
    // than 'top' and 'left', especially across browsers or with complex layouts.
    if (bottom !== null) markerWrapper.style.bottom = bottom;
    if (right !== null) markerWrapper.style.right = right;
    if (transform !== null) markerWrapper.style.transform = transform;

    container.appendChild(markerWrapper);
}

/**
 * Creates a set of markers based on a predefined pattern array.
 * @param {Array<Object>} pattern - An array of marker position and transform objects.
 * @param {string} imgPath - The SVG string for the marker shape.
 * @param {string} widthValue - The CSS width value for the markers.
 * @param {Array<string>} commonClasses - An array of CSS classes to apply to each marker.
 */
function createMarkersFromPattern(pattern, imgPath, widthValue, commonClasses) {
    pattern.forEach(p => {
        addMarkerToContainer(preview, imgPath, p.top || null, p.left || null, p.bottom || null, p.right || null, p.transform || null, widthValue, commonClasses);
    });
}

/**
 * Creates and adds the four corner edge markers to the preview area.
 */
function createedgemarkers(edgeSizeFactor) {
  const baseEdgeWidthPercent = 5;
  const actualEdgeWidth = (baseEdgeWidthPercent * edgeSizeFactor) + '%';
  const commonClasses = ["marker", "edgemarker"];
  createMarkersFromPattern(edgeMarkerPattern, imgPathEdge, actualEdgeWidth, commonClasses);
}

/**
 * Creates scrolling markers, stores references, and sets initial positions.
 * @param {string} imgPath - The SVG string for the marker shape.
 * @param {string} widthValue - The CSS width value for the markers (e.g., '2%').
 */
function createScrollingMarkers(imgPath, widthValue) {
  if (!scrollingMarkerContainer) return;

  // Clear previous scrolling markers *inside the container* and clear the element array
  while (scrollingMarkerContainer.hasChildNodes()) {
      scrollingMarkerContainer.removeChild(scrollingMarkerContainer.firstChild);
  }
  scrollingMarkerElements = []; // Clear the array

  const scrollMode = ScrollingMarkers.value;
  let basePattern;

  if (scrollMode === 'horizontal') {
    // Horizontal: markers scroll left/right, so top positions are fixed.
    // Use thirds (33%/67%) to avoid static markers at 25%/50%/75%.
    basePattern = [
      { top: '33%', left: '0%' },  { top: '33%', left: '50%' },
      { top: '67%', left: '0%' },  { top: '67%', left: '50%' }
    ];
  } else {
    // Vertical: markers scroll up/down, so left positions are fixed.
    // Use thirds (33%/67%) to avoid static markers at 25%/50%/75%.
    basePattern = [
      { top: '0%', left: '33%' },   { top: '0%', left: '67%' },
      { top: '50%', left: '33%' },  { top: '50%', left: '67%' },
      { top: '100%', left: '33%' }, { top: '100%', left: '67%' },
    ];
  }

  const commonClasses = ["marker", "scrollingmarker"];

  basePattern.forEach(pos => {
      const markerWrapper = document.createElement('div');
      markerWrapper.classList.add(...commonClasses);
      markerWrapper.style.position = 'absolute';
      markerWrapper.style.top = '0'; // Provide a consistent origin for transforms
      markerWrapper.style.left = '0'; // Provide a consistent origin for transforms
      markerWrapper.style.width = widthValue;
      // Center the marker element itself. The positional transform will be applied in updateScrollingMarkerPositions.
      markerWrapper.style.transform = 'translate(-50%, -50%)';
      // Ensure pointer events pass through individual markers too
      markerWrapper.style.pointerEvents = 'none';
      // Eliminate potential spacing issues from text/line-height properties
      markerWrapper.style.fontSize = '0';
      markerWrapper.style.lineHeight = '0';

      const svgElement = createStyledSvgElement(imgPath, MarkerColor.value);
      if (svgElement) {
          markerWrapper.appendChild(svgElement);
      } else if (imgPath) {
          console.warn(`Could not create SVG for scrolling marker. imgPath type: ${typeof imgPath}, first 50 chars: ${String(imgPath).substring(0,50)}`);
      }

      // Store reference and base position (convert percentage string to number 0-1)
      const topPercent = parseFloat(pos.top) / 100;
      const leftPercent = parseFloat(pos.left) / 100;
      scrollingMarkerElements.push({
          element: markerWrapper,
          baseTopPercent: topPercent,
          baseLeftPercent: leftPercent
      });

      // Append to container (position will be set by updateScrollingMarkerPositions)
      scrollingMarkerContainer.appendChild(markerWrapper);
  });
}

/**
 * Sets up the scrolling marker container, creates scrolling markers, and initializes the ResizeObserver.
 * @param {string} currentScrollMode - The current scrolling mode ('horizontal', 'vertical', 'none').
 * @param {string} markerImgPath - The SVG string for the markers.
 * @param {string} markerWidthValue - The CSS width for the markers.
 */
function setupScrollingMarkersAndObserver(currentScrollMode, markerImgPath, markerWidthValue) {
    if (currentScrollMode === 'none') {
        scrollingMarkerContainer = null; // Ensure it's reset if mode changed
        // If an observer was attached to a previous container, it's cleaned up at the start of updatePreview
        return;
    }

    scrollingMarkerContainer = document.createElement('div');
    scrollingMarkerContainer.id = 'scrollingMarkerContainer';
    Object.assign(scrollingMarkerContainer.style, {
        position: 'absolute',
        top: '0px',
        left: '0px',
        width: '100%',
        height: '100%',
        overflow: 'hidden',
        pointerEvents: isInActivePreviewMode ? 'auto' : 'none',
        boxSizing: 'border-box',
        padding: '0px',
        border: 'none'
    });

    createScrollingMarkers(markerImgPath, markerWidthValue); // Populates scrollingMarkerElements
    preview.appendChild(scrollingMarkerContainer);

    // Setup ResizeObserver for the new container
    scrollingMarkerContainerResizeObserver.observe(scrollingMarkerContainer);
}

/**
 * Clears and regenerates the entire marker preview based on current settings.
 * Includes setup for infinite scrolling markers.
 */
function updatePreview() {
  // --- START: Observer Cleanup for OLD container ---
  if (scrollingMarkerContainerResizeObserver && scrollingMarkerContainer) {
    // If an observer exists and was observing the previous container instance,
    // unobserve it before the `scrollingMarkerContainer` variable is reassigned or the element removed.
    scrollingMarkerContainerResizeObserver.unobserve(scrollingMarkerContainer);
  }
  // --- END: Observer Cleanup ---
  // Toggle preview mode via CSS classes (static styles defined in input.css)
  if (isInActivePreviewMode) {
    preview.classList.add('preview-active');
    preview.classList.remove('preview-background');
  } else {
    preview.classList.add('preview-background');
    preview.classList.remove('preview-active');
  }

  // Stop any ongoing momentum scrolling before clearing
  if (isMomentumScrolling) {
      cancelAnimationFrame(animationFrameId);
      isMomentumScrolling = false;
  }

  // --- START: Clear existing markers ---
  const childrenToRemove = Array.from(preview.children).filter(el =>
      el.classList.contains('marker') || el.id === 'scrollingMarkerContainer'
  );
  childrenToRemove.forEach(child => preview.removeChild(child));
  // --- END: Clear existing markers ---

  preview.style.backgroundColor = BGColor.value;
  preview.style.filter = "brightness(" + brightnessInput.value + "%)";

  // --- START: Reset Scrolling State ---
  scrollingMarkerContainer = null;
  scrollingMarkerElements = []; // Clear the element array
  currentScrollX = 0;
  currentScrollY = 0;
  velocityX = 0; // Reset velocity
  velocityY = 0;
  const scrollMode = ScrollingMarkers.value;
  // --- END: Reset Scrolling State ---

  // Determine static marker SVG and width
  imgPath = getMarkerSvg(MarkerType.value);
  width = getMarkerWidth(MarkerSize.value);

  // Determine scrolling marker SVG and width (independent settings)
  const scrollingImgPath = getMarkerSvg(ScrollingMarkerType.value);
  const scrollingWidth = getMarkerWidth(ScrollingMarkerSize.value);

  // Determine edge marker SVG
  const edgeMarkerType = edgemarkers.value;
  switch (edgeMarkerType) {
      case "none": imgPathEdge = null; break;
      case "corner": imgPathEdge = marker_cross; break;
      case "semicircle": imgPathEdge = marker_circle; break;
  };

  const edgeMarkerSizeFactor = parseInt(EdgeMarkerSize.value, 10);

  // Calculate Screen Marker Offset
  let screenMarkerOffsetPercent = 3;
  if (imgPathEdge && (edgeMarkerType === 'semicircle' || edgeMarkerType === 'corner')) {
      screenMarkerOffsetPercent = Math.max(3, (edgeMarkerSizeFactor * 5.5));
  }
  const screenMarkerOffset = screenMarkerOffsetPercent + '%';
  
  // Initialize ResizeObserver if it doesn't exist
  // This observer will be attached to the scrollingMarkerContainer if one is created.
  if (!scrollingMarkerContainerResizeObserver) {
      scrollingMarkerContainerResizeObserver = new ResizeObserver(entries => {
          for (let entry of entries) {
              if (entry.target === scrollingMarkerContainer && scrollingMarkerContainer.offsetWidth > 0 && scrollingMarkerContainer.offsetHeight > 0) {
                  updateScrollingMarkerPositions();
              }
          }
      });
  }

  // Setup scrolling markers and their container (if scrollMode is not 'none')
  setupScrollingMarkersAndObserver(scrollMode, scrollingImgPath, scrollingWidth);

  // --- START: Add Static (Screen) and Edge Markers (AFTER scrolling container) ---
  const commonScreenMarkerClasses = ["marker", "screenmarker"];
  if (MarkerDensity.value > 0) {
      createMarkersFromPattern(markerPatterns.density1(screenMarkerOffset), imgPath, width, commonScreenMarkerClasses);
      if (MarkerDensity.value >= 2) {
          createMarkersFromPattern(markerPatterns.density2(screenMarkerOffset), imgPath, width, commonScreenMarkerClasses);
          if (MarkerDensity.value == 3) {
              createMarkersFromPattern(markerPatterns.density3(), imgPath, width, commonScreenMarkerClasses);
          }
      }
  }
  if (imgPathEdge) {
      createedgemarkers(edgeMarkerSizeFactor);
  }
  // --- END: Add Static and Edge Markers ---
}


/**
 * Initiates the preview mode, hiding the config UI and showing active markers.
 */
function startPreSet() {
  isInActivePreviewMode = true;
  requestWakeLock();

  if (configPanel) {
    configPanel.classList.add('config-panel-hidden');
  }

  updatePreview(); // Refresh preview to active mode
  // --- START: Fullscreen Request ---
  const elementToFullscreen = document.documentElement; // Target the whole page for fullscreen

  if (elementToFullscreen.requestFullscreen) {
    elementToFullscreen.requestFullscreen({ navigationUI: "hide" }).catch(err => {
      // Optional: Log error if it's not a user denial (NotAllowedError)
      if (err.name !== "NotAllowedError") {
        console.warn(`Fullscreen request failed: ${err.message} (${err.name})`);
      }
    });
  } else if (elementToFullscreen.webkitRequestFullscreen) { // Safari, older Chrome/Edge
    elementToFullscreen.webkitRequestFullscreen({ navigationUI: "hide" });
  } else if (elementToFullscreen.mozRequestFullScreen) { // Firefox (older versions might not support options)
    // Modern Firefox supports the options object.
    elementToFullscreen.mozRequestFullScreen({ navigationUI: "hide" });
  } else if (elementToFullscreen.msRequestFullscreen) { // IE11/older Edge
    elementToFullscreen.msRequestFullscreen(); // IE11 does not support the options object
  }
  // If no Fullscreen API method is supported, or if the request is denied,
  // the application continues without entering fullscreen, as per the requirement.
  // --- END: Fullscreen Request ---
}
