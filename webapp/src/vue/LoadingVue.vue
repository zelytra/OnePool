<template>
  <div ref="scene" class="scene">
    <img v-for="ball in balls" :key="ball.id" :src="ball.image" class="ball"
         :style="{ left: ball.position.x + 'px', top: ball.position.y + 'px' }" alt=""/>
    <img ref="whiteBall" src="@/assets/icons/balls/white.svg" class="ball white-ball" alt=""/>
  </div>
</template>

<script setup lang="ts">
import {ref, onMounted} from 'vue';
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
  {id: 1, image: eleven, position: {x: 300, y: 150}},
  {id: 2, image: two, position: {x: 340, y: 150}},
  {id: 3, image: thirteen, position: {x: 380, y: 150}},
  {id: 4, image: four, position: {x: 320, y: 190}},
  {id: 5, image: five, position: {x: 360, y: 190}},
  {id: 6, image: six, position: {x: 400, y: 190}},
  {id: 7, image: ten, position: {x: 340, y: 230}},
  {id: 8, image: three, position: {x: 380, y: 230}},
  {id: 9, image: fourteen, position: {x: 420, y: 230}},
  {id: 10, image: fifteen, position: {x: 360, y: 270}},
  {id: 11, image: eight, position: {x: 400, y: 270}},
  {id: 12, image: one, position: {x: 380, y: 310}},
  {id: 13, image: seven, position: {x: 420, y: 310}},
  {id: 14, image: twelve, position: {x: 440, y: 310}},
  {id: 15, image: nine, position: {x: 460, y: 310}},
]);

onMounted(async () => {
  // Preload all images
  try {
    await Promise.all(balls.value.map(ball => preloadImage(ball.image)));
    await preloadImage(white);

    // Initialize Matter.js engine
    const {Engine, Render, Runner, Bodies, Composite, Events} = Matter;

    const engine = Engine.create();
    engine.gravity.y = 0;
    const world = engine.world;

    const render = Render.create({
      element: scene.value,
      engine: engine,
      options: {
        width: 800,
        height: 600,
        wireframes: false,
        background: 'transparent'
      }
    });

    Render.run(render);
    const runner = Runner.create();
    Runner.run(runner, engine);

    // Add walls with appropriate thickness and friction to simulate a table surface
    const walls = [
      Bodies.rectangle(400, 0, 800, 50, {isStatic: true, friction: 0.01}), // top
      Bodies.rectangle(400, 600, 800, 50, {isStatic: true, friction: 0.01}), // bottom
      Bodies.rectangle(800, 300, 50, 600, {isStatic: true, friction: 0.01}), // right
      Bodies.rectangle(0, 300, 50, 600, {isStatic: true, friction: 0.01}) // left
    ];
    Composite.add(world, walls);

    // Add balls with low friction to simulate rolling
    const ballRadius = 20;
    const matterBalls = balls.value.map(ball => {
      return Bodies.circle(ball.position.x, ball.position.y, ballRadius, {
        restitution: 0.9,
        friction: 0.05,
        frictionAir: 0.01,
        render: {
          sprite: {
            texture: ball.image,
            xScale: 2 * ballRadius / 40,  // Assuming ball images are 40x40 pixels
            yScale: 2 * ballRadius / 40   // Adjust scale to match ball size
          }
        }
      });
    });

    const whiteBallBody = Bodies.circle(400, 500, ballRadius, {
      restitution: 0.9,
      friction: 0.05,
      frictionAir: 0.01,
      render: {
        sprite: {
          texture: white,
          xScale: 2 * ballRadius / 40,  // Assuming ball images are 40x40 pixels
          yScale: 2 * ballRadius / 40   // Adjust scale to match ball size
        }
      }
    });
    Composite.add(world, [...matterBalls, whiteBallBody]);

    // Apply force to the white ball
    Matter.Body.applyForce(whiteBallBody, {x: whiteBallBody.position.x, y: whiteBallBody.position.y}, {x: 0, y: -0.05});

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
  } catch (error) {
    console.error('Error loading images:', error);
  }
});
</script>

<style scoped>
.scene {
  position: relative;
  width: 800px;
  height: 600px;
  background: green;
}

.ball {
  position: absolute;
  width: 40px;
  height: 40px;
  border-radius: 50%;
}
</style>
