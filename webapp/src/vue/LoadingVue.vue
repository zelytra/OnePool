<template>
  <div ref="scene" id="scene"/>
</template>

<script setup lang="ts">
import {ref, onMounted, nextTick} from 'vue';
import * as Matter from "matter-js";

// Import ball images
import eleven from '@/assets/icons/balls/eleven.svg';
import two from '@/assets/icons/balls/two.svg';
import thirteen from '@/assets/icons/balls/thirteen.svg';
import four from '@/assets/icons/balls/four.svg';
import five from '@/assets/icons/balls/five.svg';
import six from '@/assets/icons/balls/six.svg';
import ten from '@/assets/icons/balls/ten.svg';
import three from '@/assets/icons/balls/three.svg';
import fourteen from '@/assets/icons/balls/fourteen.svg';
import fifteen from '@/assets/icons/balls/fifteen.svg';
import eight from '@/assets/icons/balls/eight.svg';
import one from '@/assets/icons/balls/one.svg';
import seven from '@/assets/icons/balls/seven.svg';
import twelve from '@/assets/icons/balls/twelve.svg';
import nine from '@/assets/icons/balls/nine.svg';
import white from '@/assets/icons/balls/white.svg';

// Preload images
const preloadImage = (src: any) => {
  return new Promise((resolve, reject) => {
    const img = new Image();
    img.src = src;
    img.onload = () => resolve(img);
    img.onerror = reject;
  });
};

// References to the scene and white ball elements
const scene = ref(null);
const whiteBall = ref(null);

// Ball data
const balls = ref([
  {id: 1, image: one, position: {x: 0, y: 0}}, // Apex ball
  {id: 2, image: two, position: {x: 0, y: 0}},
  {id: 3, image: thirteen, position: {x: 0, y: 0}},
  {id: 4, image: four, position: {x: 0, y: 0}},
  {id: 5, image: eight, position: {x: 0, y: 0}},
  {id: 6, image: six, position: {x: 0, y: 0}},
  {id: 7, image: ten, position: {x: 0, y: 0}},
  {id: 8, image: three, position: {x: 0, y: 0}},
  {id: 9, image: fourteen, position: {x: 0, y: 0}},
  {id: 10, image: fifteen, position: {x: 0, y: 0}},
  {id: 11, image: five, position: {x: 0, y: 0}},
  {id: 12, image: eleven, position: {x: 0, y: 0}},
  {id: 13, image: seven, position: {x: 0, y: 0}},
  {id: 14, image: twelve, position: {x: 0, y: 0}},
  {id: 15, image: nine, position: {x: 0, y: 0}},
]);

const isMatterReady = ref(false);

const setupPositions = (width: number, _height: number) => {
  const startX = width / 2;
  const startY = 200;
  const offsetX = 20;
  const offsetY = 40;

  balls.value[0].position = {x: startX, y: startY};
  balls.value[1].position = {x: startX - offsetX, y: startY + offsetY};
  balls.value[2].position = {x: startX + offsetX, y: startY + offsetY};
  balls.value[3].position = {x: startX - 2 * offsetX, y: startY + 2 * offsetY};
  balls.value[4].position = {x: startX, y: startY + 2 * offsetY};
  balls.value[5].position = {x: startX + 2 * offsetX, y: startY + 2 * offsetY};
  balls.value[6].position = {x: startX - 3 * offsetX, y: startY + 3 * offsetY};
  balls.value[7].position = {x: startX - offsetX, y: startY + 3 * offsetY};
  balls.value[8].position = {x: startX + offsetX, y: startY + 3 * offsetY};
  balls.value[9].position = {x: startX + 3 * offsetX, y: startY + 3 * offsetY};
  balls.value[10].position = {x: startX - 4 * offsetX, y: startY + 4 * offsetY};
  balls.value[11].position = {x: startX - 2 * offsetX, y: startY + 4 * offsetY};
  balls.value[12].position = {x: startX, y: startY + 4 * offsetY};
  balls.value[13].position = {x: startX + 2 * offsetX, y: startY + 4 * offsetY};
  balls.value[14].position = {x: startX + 4 * offsetX, y: startY + 4 * offsetY};
};

async function initializeScene() {
  // Preload all images
  try {
    await nextTick();
    await Promise.all(balls.value.map(ball => preloadImage(ball.image)));
    await preloadImage(white);

    const element = scene.value;
    if (!element) return;
    const width = element.clientWidth;
    const height = element.clientHeight;

    // Initialize Matter.js engine
    const {Engine, Render, Runner, Bodies, Composite, Events} = Matter;

    let engine = Engine.create();
    engine.gravity.y = 0;
    const world = engine.world;

    const render = Render.create({
      element: scene.value,
      engine: engine,
      options: {
        width: width,
        height: height,
        wireframes: false,
        background: 'transparent'
      }
    });

    Render.run(render);
    const runner = Runner.create();
    Runner.run(runner, engine);

    // Setup positions for balls and white ball
    setupPositions(width, height);

    const ballRadius = 20; // Half of 144px
    const matterBalls = balls.value.map(ball => {
      return Bodies.circle(ball.position.x, ball.position.y, ballRadius, {
        restitution: 0.9,
        friction: 0.05,
        frictionAir: 0.01,
        render: {
          sprite: {
            texture: ball.image,
            xScale: ballRadius * 2 / 140,  // Adjust scale to match ball size
            yScale: ballRadius * 2 / 140   // Adjust scale to match ball size
          }
        }
      });
    });

    const whiteBallBody = Bodies.circle(width / 2, 20, ballRadius, {
      restitution: 0.9,
      friction: 0.05,
      frictionAir: 0.01,
      render: {
        sprite: {
          texture: white,
          xScale: ballRadius * 2 / 140,  // Adjust scale to match ball size
          yScale: ballRadius * 2 / 140   // Adjust scale to match ball size
        }
      }
    });
    Composite.add(world, [...matterBalls, whiteBallBody]);

    // Apply force to the white ball (if needed)
    Matter.Body.applyForce(whiteBallBody, {x: whiteBallBody.position.x, y: whiteBallBody.position.y}, {x: 0, y: 0.05});

    // Update Vue ball positions on each tick
    Events.on(engine, 'afterUpdate', () => {
      balls.value.forEach((ball, index) => {
        ball.position.x = matterBalls[index].position.x;
        ball.position.y = matterBalls[index].position.y;
      });
      if (whiteBall.value) {
        whiteBall.value.style.left = whiteBallBody.position.x + 'px';
        whiteBall.value.style.top = whiteBallBody.position.y + 'px';
      }
    });

    // Add reset logic
    setTimeout(() => {
      Engine.clear(engine);
      Render.stop(render);
      Runner.stop(runner);
      render.canvas.remove();
      render.textures = {};
      initializeScene(); // Reinitialize scene
    }, 10000); // Duration of the fade-out animation

    isMatterReady.value = true;
  } catch (error) {
    console.error('Error loading images:', error);
  }
};


onMounted(() => {
  initializeScene();
});
</script>

<style scoped lang="scss">
#scene {
  position: absolute;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
  backdrop-filter: blur(2px);
  z-index: 99;
  animation: fadeOut 10s ease-in-out infinite, fadeIn 10s ease-in-out infinite;
}

@keyframes fadeOut {
  0% {
    opacity: 1;
  }
  80% {
    opacity: 1;
  }
  100% {
    opacity: 0;
  }
}

@keyframes fadeIn {
  0% {
    opacity: 0;
  }
  10% {
    opacity: 1;
  }
  100% {
    opacity: 1;
  }
}

</style>
